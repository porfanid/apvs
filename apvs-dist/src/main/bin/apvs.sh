#!/usr/bin/env bash  
#
# Startup script for apvs under *nix systems (it works under NT/cygwin too).

# To get the service to restart correctly on reboot, uncomment below (3 lines):
# ========================
# chkconfig: 3 99 99
# description: Apvs webserver
# processname: apvs
# ========================

# Configuration files
#
# /etc/default/apvs
#   If it exists, this is read at the start of script. It may perform any 
#   sequence of shell commands, like setting relevant environment variables.
#
# $HOME/.apvsrc
#   If it exists, this is read at the start of script. It may perform any 
#   sequence of shell commands, like setting relevant environment variables.
#
# /etc/apvs.conf
#   If found, and no configurations were given on the command line,
#   the file will be used as this script's configuration. 
#   Each line in the file may contain:
#     - A comment denoted by the pound (#) sign as first non-blank character.
#     - The path to a regular file, which will be passed to apvs as a 
#       config.xml file.
#     - The path to a directory. Each *.xml file in the directory will be
#       passed to apvs as a config.xml file.
#
#   The files will be checked for existence before being passed to apvs.
#
# $APVS_HOME/etc/apvs.xml
#   If found, used as this script's configuration file, but only if
#   /etc/apvs.conf was not present. See above.
#   
# Configuration variables
#
# JAVA
#   Command to invoke Java. If not set, java (from the PATH) will be used.
#
# JAVA_OPTIONS
#   Extra options to pass to the JVM
#
# APVS_HOME
#   Where Apvs is installed. If not set, the script will try go
#   guess it by first looking at the invocation path for the script,
#   and then by looking in standard locations as $HOME/opt/apvs
#   and /opt/apvs. The java system property "apvs.home" will be
#   set to this value for use by configure.xml files, f.e.:
#
#    <Arg><Property name="apvs.home" default="."/>/webapps/apvs.war</Arg>
#
# APVS_PORT
#   Override the default port for Apvs servers. If not set then the
#   default value in the xml configuration file will be used. The java
#   system property "apvs.port" will be set to this value for use in
#   configure.xml files. For example, the following idiom is widely
#   used in the demo config files to respect this property in Listener
#   configuration elements:
#
#    <Set name="Port"><Property name="apvs.port" default="8080"/></Set>
#
#   Note: that the config file could ignore this property simply by saying:
#
#    <Set name="Port">8080</Set>
#
# APVS_RUN
#   Where the apvs.pid file should be stored. It defaults to the
#   first available of /var/run, /usr/var/run, and /tmp if not set.
#  
# APVS_PID
#   The Apvs PID file, defaults to $APVS_RUN/apvs.pid
#   
# APVS_ARGS
#   The default arguments to pass to apvs.
#
# APVS_USER
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
for CONFIG in /etc/default/apvs{,1} $HOME/.apvsrc; do
  if [ -f "$CONFIG" ] ; then 
    readConfig "$CONFIG"
  fi
done


##################################################
# Set tmp if not already set.
##################################################
TMPDIR=${TMPDIR:-/tmp}

##################################################
# Apvs's hallmark
##################################################
APVS_INSTALL_TRACE_FILE="etc/apvs.xml"


##################################################
# Try to determine APVS_HOME if not set
##################################################
if [ -z "$APVS_HOME" ] 
then
  APVS_SH=$0
  case "$APVS_SH" in
    /*)   ;;
    ./*)  ;;
    *)    APVS_SH=./$APVS_SH ;;
  esac
  APVS_HOME=${APVS_SH%/*/*}

  if [ ! -f "${APVS_SH%/*/*}/$APVS_INSTALL_TRACE_FILE" ]
  then 
    APVS_HOME=
  fi
fi


##################################################
# if no APVS_HOME, search likely locations.
##################################################
if [ -z "$APVS_HOME" ] ; then
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
  APVS_DIR_NAMES=(
        "apvs-7"
        "apvs7"
        "apvs-7.*"
        "apvs"
        "Apvs-7"
        "Apvs7"
        "Apvs-7.*"
        "Apvs"
        )
        
  for L in "${STANDARD_LOCATIONS[@]}"
  do
    for N in "${APVS_DIR_NAMES[@]}"
    do
      POSSIBLE_APVS_HOME=("$L/"$N)
      if [ ! -d "$POSSIBLE_APVS_HOME" ]
      then
        # Not a directory. skip.
        unset POSSIBLE_APVS_HOME
      elif [ ! -f "$POSSIBLE_APVS_HOME/$APVS_INSTALL_TRACE_FILE" ]
      then
        # Trace file not found. skip.
        unset POSSIBLE_APVS_HOME
      else
        # Good hit, Use it
        APVS_HOME=$POSSIBLE_APVS_HOME
        # Break out of APVS_DIR_NAMES loop
        break
      fi
    done
    if [ -n "$POSSIBLE_APVS_HOME" ]
    then
      # We have found our APVS_HOME
      # Break out of STANDARD_LOCATIONS loop
      break
    fi
  done
fi


##################################################
# No APVS_HOME yet? We're out of luck!
##################################################
if [ -z "$APVS_HOME" ]; then
  echo "** ERROR: APVS_HOME not set, you need to set it or install in a standard location" 
  exit 1
fi

cd "$APVS_HOME"
APVS_HOME=$PWD


#####################################################
# Check that apvs is where we think it is
#####################################################
if [ ! -r "$APVS_HOME/$APVS_INSTALL_TRACE_FILE" ] 
then
  echo "** ERROR: Oops! Apvs doesn't appear to be installed in $APVS_HOME"
  echo "** ERROR:  $APVS_HOME/$APVS_INSTALL_TRACE_FILE is not readable!"
  exit 1
fi

##################################################
# Try to find this script's configuration file,
# but only if no configurations were given on the
# command line.
##################################################
if [ -z "$APVS_CONF" ] 
then
  if [ -f /etc/apvs.conf ]
  then
    APVS_CONF=/etc/apvs.conf
  elif [ -f "$APVS_HOME/etc/apvs.conf" ]
  then
    APVS_CONF=$APVS_HOME/etc/apvs.conf
  fi
fi

##################################################
# Get the list of config.xml files from apvs.conf
##################################################
if [ -z "$CONFIGS" ] && [ -f "$APVS_CONF" ] && [ -r "$APVS_CONF" ] 
then
  while read -r CONF
  do
    if expr "$CONF" : '#' >/dev/null ; then
      continue
    fi

    if [ -d "$CONF" ] 
    then
      # assume it's a directory with configure.xml files
      # for example: /etc/apvs.d/
      # sort the files before adding them to the list of CONFIGS
      for XMLFILE in "$CONF/"*.xml
      do
        if [ -r "$XMLFILE" ] && [ -f "$XMLFILE" ] 
        then
          CONFIGS+=("$XMLFILE")
        else
          echo "** WARNING: Cannot read '$XMLFILE' specified in '$APVS_CONF'" 
        fi
      done
    else
      # assume it's a command line parameter (let start.jar deal with its validity)
      CONFIGS+=("$CONF")
    fi
  done < "$APVS_CONF"
fi

#####################################################
# Find a location for the pid file
#####################################################
if [ -z "$APVS_RUN" ] 
then
  APVS_RUN=$(findDirectory -w /var/run /usr/var/run /tmp)
fi

#####################################################
# Find a PID for the pid file
#####################################################
if [ -z "$APVS_PID" ] 
then
  APVS_PID="$APVS_RUN/apvs.pid"
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
# See if APVS_PORT is defined
#####################################################
if [ "$APVS_PORT" ] 
then
  JAVA_OPTIONS+=("-Dapvs.port=$APVS_PORT")
fi

#####################################################
# See if APVS_LOGS is defined
#####################################################
if [ "$APVS_LOGS" ]
then
  JAVA_OPTIONS+=("-Dapvs.logs=$APVS_LOGS")
fi

#####################################################
# Are we running on Windows? Could be, with Cygwin/NT.
#####################################################
case "`uname`" in
CYGWIN*) PATH_SEPARATOR=";";;
*) PATH_SEPARATOR=":";;
esac


#####################################################
# Add apvs properties to Java VM options.
#####################################################
JAVA_OPTIONS+=("-Dapvs.home=$APVS_HOME" "-Djava.io.tmpdir=$TMPDIR")

[ -f "$APVS_HOME/etc/start.config" ] && JAVA_OPTIONS=("-DSTART=$APVS_HOME/etc/start.config" "${JAVA_OPTIONS[@]}")

#####################################################
# This is how the Apvs server will be started
#####################################################

APVS_START=$APVS_HOME/start.jar
[ ! -f "$APVS_START" ] && APVS_START=$APVS_HOME/lib/start.jar

START_INI=$(dirname $APVS_START)/start.ini
[ -r "$START_INI" ] || START_INI=""

RUN_ARGS=(${JAVA_OPTIONS[@]} -jar "$APVS_START" $APVS_ARGS "${CONFIGS[@]}")
RUN_CMD=("$JAVA" ${RUN_ARGS[@]})

#####################################################
# Comment these out after you're happy with what 
# the script is doing.
#####################################################
if (( DEBUG ))
then
  echo "APVS_HOME     =  $APVS_HOME"
  echo "APVS_CONF     =  $APVS_CONF"
  echo "APVS_RUN      =  $APVS_RUN"
  echo "APVS_PID      =  $APVS_PID"
  echo "APVS_ARGS     =  $APVS_ARGS"
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
    echo -n "Starting Apvs: "

    if (( NO_START )); then 
      echo "Not starting apvs - NO_START=1";
      exit
    fi

    if type start-stop-daemon > /dev/null 2>&1 
    then
      unset CH_USER
      if [ -n "$APVS_USER" ]
      then
        CH_USER="-c$APVS_USER"
      fi
      if start-stop-daemon -S -p"$APVS_PID" $CH_USER -d"$APVS_HOME" -b -m -a "$JAVA" -- "${RUN_ARGS[@]}" --daemon
      then
        sleep 1
        if running "$APVS_PID"
        then
          echo "OK"
        else
          echo "FAILED"
        fi
      fi

    else

      if [ -f "$APVS_PID" ]
      then
        if running $APVS_PID
        then
          echo "Already Running!"
          exit 1
        else
          # dead pid file - remove
          rm -f "$APVS_PID"
        fi
      fi

      if [ "$APVS_USER" ] 
      then
        touch "$APVS_PID"
        chown "$APVS_USER" "$APVS_PID"
        # FIXME: Broken solution: wordsplitting, pathname expansion, arbitrary command execution, etc.
        su - "$APVS_USER" -c "
          exec ${RUN_CMD[*]} --daemon &
          disown \$!
          echo \$! > '$APVS_PID'"
      else
        "${RUN_CMD[@]}" &
        disown $!
        echo $! > "$APVS_PID"
      fi

      echo "STARTED Apvs `date`" 
    fi

    ;;

  stop)
    echo -n "Stopping Apvs: "
    if type start-stop-daemon > /dev/null 2>&1; then
      start-stop-daemon -K -p"$APVS_PID" -d"$APVS_HOME" -a "$JAVA" -s HUP
      
      TIMEOUT=30
      while running "$APVS_PID"; do
        if (( TIMEOUT-- == 0 )); then
          start-stop-daemon -K -p"$APVS_PID" -d"$APVS_HOME" -a "$JAVA" -s KILL
        fi

        sleep 1
      done

      rm -f "$APVS_PID"
      echo OK
    else
      PID=$(cat "$APVS_PID" 2>/dev/null)
      kill "$PID" 2>/dev/null
      
      TIMEOUT=30
      while running $APVS_PID; do
        if (( TIMEOUT-- == 0 )); then
          kill -KILL "$PID" 2>/dev/null
        fi

        sleep 1
      done

      rm -f "$APVS_PID"
      echo OK
    fi

    ;;

  restart)
    APVS_SH=$0
    if [ ! -f $APVS_SH ]; then
      if [ ! -f $APVS_HOME/bin/apvs.sh ]; then
        echo "$APVS_HOME/bin/apvs.sh does not exist."
        exit 1
      fi
      APVS_SH=$APVS_HOME/bin/apvs.sh
    fi

    "$APVS_SH" stop "$@"
    "$APVS_SH" start "$@"

    ;;

  supervise)
    #
    # Under control of daemontools supervise monitor which
    # handles restarts and shutdowns via the svc program.
    #
    exec "${RUN_CMD[@]}"

    ;;

  run|demo)
    echo "Running Apvs: "

    if [ -f "$APVS_PID" ]
    then
      if running "$APVS_PID"
      then
        echo "Already Running!"
        exit 1
      else
        # dead pid file - remove
        rm -f "$APVS_PID"
      fi
    fi

    exec "${RUN_CMD[@]}"

    ;;

  check)
    echo "Checking arguments to Apvs: "
    echo "APVS_HOME     =  $APVS_HOME"
    echo "APVS_CONF     =  $APVS_CONF"
    echo "APVS_RUN      =  $APVS_RUN"
    echo "APVS_PID      =  $APVS_PID"
    echo "APVS_PORT     =  $APVS_PORT"
    echo "APVS_LOGS     =  $APVS_LOGS"
    echo "START_INI      =  $START_INI"
    echo "CONFIGS        =  ${CONFIGS[*]}"
    echo "JAVA_OPTIONS   =  ${JAVA_OPTIONS[*]}"
    echo "JAVA           =  $JAVA"
    echo "CLASSPATH      =  $CLASSPATH"
    echo "RUN_CMD        =  ${RUN_CMD[*]}"
    echo
    
    if [ -f "$APVS_PID" ]
    then
      echo "Apvs running pid=$(< "$APVS_PID")"
      exit 0
    fi
    exit 1

    ;;

  *)
    usage

    ;;
esac

exit 0
