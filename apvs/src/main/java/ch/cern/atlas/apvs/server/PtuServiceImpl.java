package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.PtuService;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuReader;
import ch.cern.atlas.apvs.ptu.server.PtuWriter;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PtuServiceImpl extends ResponsePollService implements PtuService,
		Runnable {

	private static final String name = "PtuSocket";
	private static final int DEFAULT_PORT = 4005;
	private static final int RECONNECT_INTERVAL = 20000;

	private String host = null;
	private int port = DEFAULT_PORT;
	private boolean stopped = false;
	private Socket socket;
	private String ptuUrl;
	private PtuReader ptuReader;

	private RemoteEventBus eventBus;

	public PtuServiceImpl() {
		System.out.println("Creating PtuService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("Starting PtuService...");

		ServerSettingsChangedEvent.subscribe(eventBus,
				new ServerSettingsChangedEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings
									.get(ServerSettings.settingNames[0]);
							if ((url != null) && !url.equals(ptuUrl)) {
								ptuUrl = url;
								String[] s = ptuUrl.split(":", 2);
								host = s[0];
								port = s.length > 1 ? Integer.parseInt(s[1])
										: DEFAULT_PORT;

								if (socket != null) {
									System.err.println("Interrupting PTU");
									try {
										socket.close();
									} catch (IOException e) {
										// ignored
									}
								}
							}
						}
					}
				});

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {

		boolean showError = true;

		while (!stopped) {
			if ((ptuReader == null) && (host != null)) {
				try {
					if (showError) {
						System.out.println("Trying to connect to " + name
								+ " on " + host + ":" + port);
					}
					socket = new Socket(host, port);
					showError = true;
					System.out.println("Connected to " + name + " on " + host
							+ ":" + port);

					PtuWriter ptuWriter = new PtuWriter(socket);
					Thread writerThread = new Thread(ptuWriter);
					writerThread.start();

					ptuReader = new PtuReader(eventBus, socket);

					Thread readerThread = new Thread(ptuReader);
					readerThread.start();
					readerThread.join();
					socket = null;
				} catch (UnknownHostException e) {
					if (showError) {
						System.err.println(getClass() + " " + e);
						showError = false;
					}
				} catch (ConnectException e) {
					if (showError) {
						System.err.println("Could not connect to " + name
								+ " on " + host + ":" + port
								+ ", retrying in a while...");
						showError = false;
					}
				} catch (IOException e) {
					if (showError) {
						System.err.println(getClass() + " " + e);
						showError = false;
					}
				} catch (InterruptedException e) {
					System.err.println(getClass() + " " + e);
				}

				if (ptuReader != null) {
					ptuReader.close();
				}
				ptuReader = null;
			}

			// System.err.println("Sleep");
			try {
				Thread.sleep(RECONNECT_INTERVAL);
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		stopped = true;

		if (ptuReader != null) {
			ptuReader.close();
		}
	}

	@Override
	public Ptu getPtu(int ptuId) {
		if (ptuReader == null) return null;
		
		return ptuReader.getPtu(ptuId);
	}
}
