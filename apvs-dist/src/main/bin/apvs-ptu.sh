#!/usr/bin/env bash  
#
# Startup script for apvs-ptu-ptu under *nix systems (it works under NT/cygwin too).

# To get the service to restart correctly on reboot, uncomment below (3 lines):
# ========================
# chkconfig: 3 99 99
# description: Apvs-Ptu webserver
# processname: apvs-ptu-ptu
# ========================

# Configuration files
#
# /etc/default/apvs_ptu
#   If it exists, this is read at the start of script. It may perform any 
#   sequence of shell commands, like setting relevant environment variables.
#
# $HOME/.apvs-pturc
#   If it exists, this is read at the start of script. It may perform any 
#   sequence of shell commands, like setting relevant environment variables.
#
# /etc/apvs-ptu.conf
#   If found, and no configurations were given on the command line,
#   the file will be used as this script's configuration. 
#   Each line in the file may contain:
#     - A comment denoted by the pound (#) sign as first non-blank character.
#     - The path to a regular file, which will be passed to apvs-ptu as a 
#       config.xml file.
#     - The path to a directory. Each *.xml file in the directory will be
#       passed to apvs-ptu as a config.xml file.
#
#   The files will be checked for existence before being passed to apvs-ptu.
#
# $APVS_PTU_HOME/etc/apvs-ptu.xml
#   If found, used as this script's configuration file, but only if
#   /etc/apvs-ptu.conf was not present. See above.
#   
# Configuration variables
#
# JAVA
#   Command to invoke Java. If not set, java (from the PATH) will be used.
#
# JAVA_OPTIONS
#   Extra options to pass to the JVM
#
# APVS_PTU_HOME
#   Where Apvs-Ptu is installed. If not set, the script will try go
#   guess it by first looking at the invocation path for the script,
#   and then by looking in standard locations as $HOME/opt/apvs-ptu
#   and /opt/apvs-ptu. The java system property "apvs-ptu.home" will be
#   set to this value for use by configure.xml files, f.e.:
#
#    <Arg><Property name="apvs-ptu.home" default="."/>/webapps/apvs-ptu.jar</Arg>
#
# APVS_PTU_PORT
#   Override the default port for Apvs-Ptu servers. If not set then the
#   default value in the xml configuration file will be used. The java
#   system property "apvs-ptu.port" will be set to this value for use in
#   configure.xml files. For example, the following idiom is widely
#   used in the demo config files to respect this property in Listener
#   configuration elements:
#
#    <Set name="Port"><Property name="apvs-ptu.port" default="8080"/></Set>
#
#   Note: that the config file could ignore this property simply by saying:
#
#    <Set name="Port">8080</Set>
#
# APVS_PTU_RUN
#   Where the apvs-ptu.pid file should be stored. It defaults to the
#   first available of /var/run, /usr/var/run, and /tmp if not set.
#  
# APVS_PTU_PID
#   The Apvs-Ptu PID file, defaults to $APVS_PTU_RUN/apvs-ptu.pid
#   
# APVS_PTU_ARGS
#   The default arguments to pass to apvs-ptu.
#
# APVS_PTU_USER
#   if set, then used as a username to run the server as
#

usage()
{
    echo "Usage: ${0##*/} [-d] {start|stop|run|restart|check|supervise} [ CONFIGS ... ] "
    exit 1
}

[ $# -gt 0 ] || usage


##################################################
# Some utility functions
##################################################
findDirectory()
{
  local L OP=$1
  shift
  for L in "$@"; do
    [ "$OP" "$L" ] || continue 
    printf %s "$L"
    break
  done 
}

running()
{
  local PID=$(cat "$1" 2>/dev/null) || return 1
  kill -0 "$PID" 2>/dev/null
}

readConfig()
{
  (( DEBUG )) && echo "Reading $1.."
  source "$1"
}



##################################################
# Get the action & configs
##################################################
CONFIGS=()
NO_START=0
DEBUG=0

while [[ $1 = -* ]]; do
  case $1 in
    -d) DEBUG=1 ;;
  esac
  shift
done
ACTION=$1
shift

##################################################
# Read any configuration files
##################################################
for CONFIG in /etc/default/apvs-ptu{,1} $HOME/.apvs-pturc; do
  if [ -f "$CONFIG" ] ; then 
    readConfig "$CONFIG"
  fi
done


##################################################
# Set tmp if not already set.
##################################################
TMPDIR=${TMPDIR:-/tmp}

##################################################
# Apvs-Ptu's hallmark
##################################################
APVS_PTU_INSTALL_TRACE_FILE="lib/apvs-ptu.jar"


##################################################
# Try to determine APVS_PTU_HOME if not set
##################################################
if [ -z "$APVS_PTU_HOME" ] 
then
  APVS_PTU_SH=$0
  case "$APVS_PTU_SH" in
    /*)   ;;
    ./*)  ;;
    *)    APVS_PTU_SH=./$APVS_PTU_SH ;;
  esac
  APVS_PTU_HOME=${APVS_PTU_SH%/*/*}

  if [ ! -f "${APVS_PTU_SH%/*/*}/$APVS_PTU_INSTALL_TRACE_FILE" ]
  then 
    APVS_PTU_HOME=
  fi
fi


##################################################
# if no APVS_PTU_HOME, search likely locations.
##################################################
if [ -z "$APVS_PTU_HOME" ] ; then
  STANDARD_LOCATIONS=(
        "/usr/share"
        "/usr/share/java"
        "${HOME}"
        "${HOME}/src"
        "${HOME}/opt"
        "/opt"
        "/java"
        "/usr/local"
        "/usr/local/share"
        "/usr/local/share/java"
        "/home"
        )
  APVS_PTU_DIR_NAMES=(
        "apvs-ptu-1"
        "apvs-ptu1"
        "apvs-ptu-1.*"
        "apvs-ptu"
        "Apvs-Ptu-1"
        "Apvs-Ptu1"
        "Apvs-Ptu-1.*"
        "Apvs-Ptu"
        )
        
  for L in "${STANDARD_LOCATIONS[@]}"
  do
    for N in "${APVS_PTU_DIR_NAMES[@]}"
    do
      POSSIBLE_APVS_PTU_HOME=("$L/"$N)
      if [ ! -d "$POSSIBLE_APVS_PTU_HOME" ]
      then
        # Not a directory. skip.
        unset POSSIBLE_APVS_PTU_HOME
      elif [ ! -f "$POSSIBLE_APVS_PTU_HOME/$APVS_PTU_INSTALL_TRACE_FILE" ]
      then
        # Trace file not found. skip.
        unset POSSIBLE_APVS_PTU_HOME
      else
        # Good hit, Use it
        APVS_PTU_HOME=$POSSIBLE_APVS_PTU_HOME
        # Break out of APVS_PTU_DIR_NAMES loop
        break
      fi
    done
    if [ -n "$POSSIBLE_APVS_PTU_HOME" ]
    then
      # We have found our APVS_PTU_HOME
      # Break out of STANDARD_LOCATIONS loop
      break
    fi
  done
fi


##################################################
# No APVS_PTU_HOME yet? We're out of luck!
##################################################
if [ -z "$APVS_PTU_HOME" ]; then
  echo "** ERROR: APVS_PTU_HOME not set, you need to set it or install in a standard location" 
  exit 1
fi

cd "$APVS_PTU_HOME"
APVS_PTU_HOME=$PWD


#####################################################
# Check that apvs-ptu is where we think it is
#####################################################
if [ ! -r "$APVS_PTU_HOME/$APVS_PTU_INSTALL_TRACE_FILE" ] 
then
  echo "** ERROR: Oops! Apvs-Ptu doesn't appear to be installed in $APVS_PTU_HOME"
  echo "** ERROR:  $APVS_PTU_HOME/$APVS_PTU_INSTALL_TRACE_FILE is not readable!"
  exit 1
fi

##################################################
# Try to find this script's configuration file,
# but only if no configurations were given on the
# command line.
##################################################
if [ -z "$APVS_PTU_CONF" ] 
then
  if [ -f /etc/apvs-ptu.conf ]
  then
    APVS_PTU_CONF=/etc/apvs-ptu.conf
  elif [ -f "$APVS_PTU_HOME/etc/apvs-ptu.conf" ]
  then
    APVS_PTU_CONF=$APVS_PTU_HOME/etc/apvs-ptu.conf
  fi
fi

##################################################
# Get the list of config.xml files from apvs-ptu.conf
##################################################
if [ -z "$CONFIGS" ] && [ -f "$APVS_PTU_CONF" ] && [ -r "$APVS_PTU_CONF" ] 
then
  while read -r CONF
  do
    if expr "$CONF" : '#' >/dev/null ; then
      continue
    fi

    if [ -d "$CONF" ] 
    then
      # assume it's a directory with configure.xml files
      # for example: /etc/apvs-ptu.d/
      # sort the files before adding them to the list of CONFIGS
      for XMLFILE in "$CONF/"*.xml
      do
        if [ -r "$XMLFILE" ] && [ -f "$XMLFILE" ] 
        then
          CONFIGS+=("$XMLFILE")
        else
          echo "** WARNING: Cannot read '$XMLFILE' specified in '$APVS_PTU_CONF'" 
        fi
      done
    else
      # assume it's a command line parameter (let apvs-ptu.jar deal with its validity)
      CONFIGS+=("$CONF")
    fi
  done < "$APVS_PTU_CONF"
fi

#####################################################
# Find a location for the pid file
#####################################################
if [ -z "$APVS_PTU_RUN" ] 
then
  APVS_PTU_RUN=$(findDirectory -w /var/run /usr/var/run /tmp)
fi

#####################################################
# Find a PID for the pid file
#####################################################
if [ -z "$APVS_PTU_PID" ] 
then
  APVS_PTU_PID="$APVS_PTU_RUN/apvs-ptu.pid"
fi

##################################################
# Setup JAVA if unset
##################################################
if [ -z "$JAVA" ]
then
  JAVA=$(which java)
fi

if [ -z "$JAVA" ]
then
  echo "Cannot find a Java JDK. Please set either set JAVA or put java (>=1.5) in your PATH." 2>&2
  exit 1
fi

#####################################################
# See if APVS_PTU_PORT is defined
#####################################################
if [ "$APVS_PTU_PORT" ] 
then
  JAVA_OPTIONS+=("-Dapvs-ptu.port=$APVS_PTU_PORT")
fi

#####################################################
# See if APVS_PTU_LOGS is defined
#####################################################
if [ "$APVS_PTU_LOGS" ]
then
  JAVA_OPTIONS+=("-Dapvs-ptu.logs=$APVS_PTU_LOGS")
fi

#####################################################
# Are we running on Windows? Could be, with Cygwin/NT.
#####################################################
case "`uname`" in
CYGWIN*) PATH_SEPARATOR=";";;
*) PATH_SEPARATOR=":";;
esac


#####################################################
# Add apvs-ptu properties to Java VM options.
#####################################################
JAVA_OPTIONS+=("-Dapvs-ptu.home=$APVS_PTU_HOME" "-Djava.io.tmpdir=$TMPDIR")

[ -f "$APVS_PTU_HOME/etc/start.config" ] && JAVA_OPTIONS=("-DSTART=$APVS_PTU_HOME/etc/start.config" "${JAVA_OPTIONS[@]}")

#####################################################
# This is how the Apvs-Ptu server will be started
#####################################################

APVS_PTU_JAR=apvs-ptu.jar
APVS_PTU_LOG=apvs-ptu.log

APVS_PTU_START=$APVS_PTU_HOME/$APVS_PTU_JAR
[ ! -f "$APVS_PTU_START" ] && APVS_PTU_START=$APVS_PTU_HOME/lib/$APVS_PTU_JAR

START_INI=$(dirname $APVS_PTU_START)/start.ini
[ -r "$START_INI" ] || START_INI=""

RUN_ARGS=(${JAVA_OPTIONS[@]} -jar "$APVS_PTU_START" $APVS_PTU_ARGS "${CONFIGS[@]}")
RUN_CMD=("$JAVA" ${RUN_ARGS[@]})

#####################################################
# Comment these out after you're happy with what 
# the script is doing.
#####################################################
if (( DEBUG ))
then
  echo "APVS_PTU_HOME     =  $APVS_PTU_HOME"
  echo "APVS_PTU_CONF     =  $APVS_PTU_CONF"
  echo "APVS_PTU_RUN      =  $APVS_PTU_RUN"
  echo "APVS_PTU_PID      =  $APVS_PTU_PID"
  echo "APVS_PTU_ARGS     =  $APVS_PTU_ARGS"
  echo "CONFIGS        =  ${CONFIGS[*]}"
  echo "JAVA_OPTIONS   =  ${JAVA_OPTIONS[*]}"
  echo "JAVA           =  $JAVA"
  echo "RUN_CMD        =  ${RUN_CMD}"
fi

##################################################
# Do the action
##################################################
case "$ACTION" in
  start)
    echo -n "Starting Apvs-Ptu: "

    if (( NO_START )); then 
      echo "Not starting apvs-ptu - NO_START=1";
      exit
    fi

    if type start-stop-daemon > /dev/null 2>&1 
    then
      unset CH_USER
      if [ -n "$APVS_PTU_USER" ]
      then
        CH_USER="-c$APVS_PTU_USER"
      fi
      if start-stop-daemon -S -p"$APVS_PTU_PID" $CH_USER -d"$APVS_PTU_HOME" -b -m -a "$JAVA" -- "${RUN_ARGS[@]}" --daemon
      then
        sleep 1
        if running "$APVS_PTU_PID"
        then
          echo "OK"
        else
          echo "FAILED"
        fi
      fi

    else

      if [ -f "$APVS_PTU_PID" ]
      then
        if running $APVS_PTU_PID
        then
          echo "Already Running!"
          exit 1
        else
          # dead pid file - remove
          rm -f "$APVS_PTU_PID"
        fi
      fi

      if [ "$APVS_PTU_USER" ] 
      then
        touch "$APVS_PTU_PID"
        chown "$APVS_PTU_USER" "$APVS_PTU_PID"
        # FIXME: Broken solution: wordsplitting, pathname expansion, arbitrary command execution, etc.
        su - "$APVS_PTU_USER" -c "
          exec ${RUN_CMD[*]} --daemon &
          disown \$!
          echo \$! > '$APVS_PTU_PID'"
      else
        "${RUN_CMD[@]}" > ${APVS_PTU_LOG} 2>&1 &
        disown $!
        echo $! > "$APVS_PTU_PID"
      fi

      echo "STARTED Apvs-Ptu `date`" 
    fi

    ;;

  stop)
    echo -n "Stopping Apvs-Ptu: "
    if type start-stop-daemon > /dev/null 2>&1; then
      start-stop-daemon -K -p"$APVS_PTU_PID" -d"$APVS_PTU_HOME" -a "$JAVA" -s HUP
      
      TIMEOUT=30
      while running "$APVS_PTU_PID"; do
        if (( TIMEOUT-- == 0 )); then
          start-stop-daemon -K -p"$APVS_PTU_PID" -d"$APVS_PTU_HOME" -a "$JAVA" -s KILL
        fi

        sleep 1
      done

      rm -f "$APVS_PTU_PID"
      echo OK
    else
      PID=$(cat "$APVS_PTU_PID" 2>/dev/null)
      kill "$PID" 2>/dev/null
      
      TIMEOUT=30
      while running $APVS_PTU_PID; do
        if (( TIMEOUT-- == 0 )); then
          kill -KILL "$PID" 2>/dev/null
        fi

        sleep 1
      done

      rm -f "$APVS_PTU_PID"
      echo OK
    fi

    ;;

  restart)
    APVS_PTU_SH=$0
    if [ ! -f $APVS_PTU_SH ]; then
      if [ ! -f $APVS_PTU_HOME/bin/apvs-ptu.sh ]; then
        echo "$APVS_PTU_HOME/bin/apvs-ptu.sh does not exist."
        exit 1
      fi
      APVS_PTU_SH=$APVS_PTU_HOME/bin/apvs-ptu.sh
    fi

    "$APVS_PTU_SH" stop "$@"
    "$APVS_PTU_SH" start "$@"

    ;;

  supervise)
    #
    # Under control of daemontools supervise monitor which
    # handles restarts and shutdowns via the svc program.
    #
    exec "${RUN_CMD[@]}"

    ;;

  run|demo)
    echo "Running Apvs-Ptu: "

    if [ -f "$APVS_PTU_PID" ]
    then
      if running "$APVS_PTU_PID"
      then
        echo "Already Running!"
        exit 1
      else
        # dead pid file - remove
        rm -f "$APVS_PTU_PID"
      fi
    fi

    exec "${RUN_CMD[@]}"

    ;;

  check)
    echo "Checking arguments to Apvs-Ptu: "
    echo "APVS_PTU_HOME     =  $APVS_PTU_HOME"
    echo "APVS_PTU_CONF     =  $APVS_PTU_CONF"
    echo "APVS_PTU_RUN      =  $APVS_PTU_RUN"
    echo "APVS_PTU_PID      =  $APVS_PTU_PID"
    echo "APVS_PTU_PORT     =  $APVS_PTU_PORT"
    echo "APVS_PTU_LOGS     =  $APVS_PTU_LOGS"
    echo "START_INI      =  $START_INI"
    echo "CONFIGS        =  ${CONFIGS[*]}"
    echo "JAVA_OPTIONS   =  ${JAVA_OPTIONS[*]}"
    echo "JAVA           =  $JAVA"
    echo "CLASSPATH      =  $CLASSPATH"
    echo "RUN_CMD        =  ${RUN_CMD[*]}"
    echo
    
    if [ -f "$APVS_PTU_PID" ]
    then
      echo "Apvs-Ptu running pid=$(< "$APVS_PTU_PID")"
      exit 0
    fi
    exit 1

    ;;

  *)
    usage

    ;;
esac

exit 0
