package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.SelectTabEvent;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.service.VideoServiceAsync;
import ch.cern.atlas.apvs.client.settings.LocalStorage;
import ch.cern.atlas.apvs.client.validation.CheckBoxField;
import ch.cern.atlas.apvs.client.validation.EmptyStringValidator;
import ch.cern.atlas.apvs.client.validation.IntegerValidator;
import ch.cern.atlas.apvs.client.validation.ListBoxField;
import ch.cern.atlas.apvs.client.validation.NotNullValidator;
import ch.cern.atlas.apvs.client.validation.OrValidator;
import ch.cern.atlas.apvs.client.validation.StringValidator;
import ch.cern.atlas.apvs.client.validation.TextAreaField;
import ch.cern.atlas.apvs.client.validation.TextBoxField;
import ch.cern.atlas.apvs.client.validation.ValidationFieldset;
import ch.cern.atlas.apvs.client.validation.ValidationForm;
import ch.cern.atlas.apvs.client.widget.ActiveColumn;
import ch.cern.atlas.apvs.client.widget.CheckboxColumn;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.ClickableTextColumn;
import ch.cern.atlas.apvs.client.widget.EditTextColumn;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.client.widget.HumanTime;
import ch.cern.atlas.apvs.client.widget.PagerHeader;
import ch.cern.atlas.apvs.client.widget.PagerHeader.TextLocation;
import ch.cern.atlas.apvs.client.widget.ScrolledDataGrid;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.ClientConstants;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.InetAddress;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.MacAddress;
import ch.cern.atlas.apvs.domain.SortOrder;
import ch.cern.atlas.apvs.domain.User;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import com.svenjacobs.gwtbootstrap3.client.ui.Button;
import com.svenjacobs.gwtbootstrap3.client.ui.Label;
import com.svenjacobs.gwtbootstrap3.client.ui.Modal;
import com.svenjacobs.gwtbootstrap3.client.ui.ModalComponent;
import com.svenjacobs.gwtbootstrap3.client.ui.ModalFooter;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.ButtonDismiss;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.FormType;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.ModalBackdrop;

public class InterventionView extends GlassPanel implements Module {

	// private Logger log = LoggerFactory.getLogger(getClass().getName());

	private ScrolledDataGrid<Intervention> table = new ScrolledDataGrid<Intervention>();
	private ScrollPanel scrollPanel;

	private ClickableTextColumn<Intervention> startTime;
	private ActiveColumn<Intervention, Object> endTime;

	private boolean selectable = false;
	private boolean sortable = true;

	private final String ONGOING_INTERVENTION = "Ongoing";
	private final String END_INTERVENTION = "End Intervention...";

	private InterventionServiceAsync interventionService;
	private VideoServiceAsync videoService;
	// private Validator validator;

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	private HorizontalPanel footer = new HorizontalPanel();
	private PagerHeader pager;
	private Button updateButton;
	private boolean showUpdate;

	private LocalStorage localStorage;
	private Boolean showTest;

	public InterventionView() {
		localStorage = LocalStorage.getInstance();
		showTest = localStorage
				.getBoolean(LocalStorage.SHOW_TEST_INTERVENTIONS);
		if (showTest == null) {
			showTest = true;
			localStorage.put(LocalStorage.SHOW_TEST_INTERVENTIONS, showTest);
		}
	}

	@Override
	public boolean configure(Element element,
			final ClientFactory clientFactory, Arguments args) {

		interventionService = clientFactory.getInterventionService();
		videoService = clientFactory.getVideoService();

		String height = args.getArg(0);

		EventBus eventBus = clientFactory.getEventBus(args.getArg(1));

		Label label = new Label();
		label.setText("No Interventions");
		table.setSize("100%", height);
		table.setEmptyTableWidget(label);

		pager = new PagerHeader(TextLocation.LEFT);
		pager.setDisplay(table);

		updateButton = new Button("Update");
		updateButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				pager.setPage(0);
				scrollPanel.setVerticalScrollPosition(scrollPanel
						.getMinimumHorizontalScrollPosition());

				table.getColumnSortList().push(
						new ColumnSortInfo(startTime, false));
				scheduler.update();
			}
		});
		updateButton.setVisible(false);
		footer.add(updateButton);

		setWidth("100%");
		add(table, CENTER);

		scrollPanel = table.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {

			@Override
			public void onScroll(ScrollEvent event) {
				if (scrollPanel.getVerticalScrollPosition() == scrollPanel
						.getMinimumVerticalScrollPosition()) {
					scheduler.update();
				}
			}
		});

		AsyncDataProvider<Intervention> dataProvider = new AsyncDataProvider<Intervention>() {

			@Override
			protected void onRangeChanged(HasData<Intervention> display) {
				// log.info("ON RANGE CHANGED " + display.getVisibleRange());

				interventionService.getRowCount(showTest,
						new AsyncCallback<Long>() {

							@Override
							public void onSuccess(Long result) {
								updateRowCount(result.intValue(), true);
							}

							@Override
							public void onFailure(Throwable caught) {
								updateRowCount(0, true);
							}
						});

				final Range range = display.getVisibleRange();

				final ColumnSortList sortList = table.getColumnSortList();
				List<SortOrder> order = new ArrayList<SortOrder>(
						sortList.size());
				for (int i = 0; i < sortList.size(); i++) {
					ColumnSortInfo info = sortList.get(i);
					order.add(new SortOrder(
							info.getColumn().getDataStoreName(), info
									.isAscending()));
				}

				if (order.isEmpty()) {
					order.add(new SortOrder("endTime", false));
				}

				interventionService.getTableData(range.getStart(),
						range.getLength(), order, showTest,
						new AsyncCallback<List<Intervention>>() {

							@Override
							public void onSuccess(List<Intervention> result) {
								updateRowData(range.getStart(), result);
							}

							@Override
							public void onFailure(Throwable caught) {
								// log.warn("RPC DB FAILED " + caught);
								updateRowCount(0, true);
							}
						});
			}
		};

		AsyncHandler columnSortHandler = new AsyncHandler(table);
		table.addColumnSortHandler(columnSortHandler);

		// startTime
		startTime = new ClickableTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return ClientConstants.dateFormatNoSeconds.format(object
						.getStartTime());
			}
		};
		startTime.setDataStoreName("startTime");
		startTime.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		startTime.setSortable(sortable);
		if (selectable) {
			startTime.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		table.addColumn(startTime, new TextHeader("Start Time"),
				pager.getHeader());

		// endTime
		EditableCell cell = new EditableCell() {
			@Override
			protected Class<? extends Cell<? extends Object>> getCellClass(
					Context context, Object value) {
				return value == END_INTERVENTION ? ButtonCell.class
						: TextCell.class;
			}
		};
		endTime = new ActiveColumn<Intervention, Object>(cell) {
			@Override
			public String getValue(Intervention object) {
				return object.getEndTime() != null ? ClientConstants.dateFormatNoSeconds
						.format(object.getEndTime()) : clientFactory
						.isSupervisor() ? END_INTERVENTION
						: ONGOING_INTERVENTION;
			}
		};
		endTime.setDataStoreName("endTime");
		endTime.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		endTime.setSortable(sortable);
		endTime.setEnabled(clientFactory.isSupervisor());
		endTime.setFieldUpdater(new FieldUpdater<Intervention, Object>() {

			@Override
			public void update(int index, Intervention intervention,
					Object value) {
				if (!clientFactory.isSupervisor()) {
					return;
				}

				if (Window.confirm("Are you sure")) {
					intervention.setEndTime(new Date());
					interventionService.updateIntervention(intervention,
							new AsyncCallback<Intervention>() {

								@Override
								public void onSuccess(Intervention result) {
									scheduler.update();
								}

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
								}

							});
					
					videoService.stopVideo(intervention, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							// ignored
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}
					});
				}
			}
		});
		table.addColumn(endTime, new TextHeader("End Time"), pager.getHeader());
		table.getColumnSortList().push(new ColumnSortInfo(startTime, false));
		table.getColumnSortList().push(new ColumnSortInfo(endTime, false));

		Header<String> interventionFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return "New Intervention...";
			}

			public boolean onPreviewColumnSortEvent(Context context,
					Element elem, NativeEvent event) {
				// events are handled, do not sort, fix for #454
				return false;
			}
		};

		final Map<Integer, User> users = new HashMap<Integer, User>();
		final Map<Integer, Device> devices = new HashMap<Integer, Device>();
		interventionFooter.setUpdater(new ValueUpdater<String>() {

			@Override
			public void update(String value) {

				ValidationFieldset fieldset = new ValidationFieldset();

				final ListBoxField userField = new ListBoxField("User",
						new NotNullValidator());
				fieldset.add(userField);

				interventionService.getUsers(true,
						new AsyncCallback<List<User>>() {

							@Override
							public void onSuccess(List<User> result) {
								for (Iterator<User> i = result.iterator(); i
										.hasNext();) {
									User user = i.next();
									users.put(user.getId(), user);
									userField.addItem(user.getDisplayName(),
											user.getId());
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								// log.warn("Caught : " + caught);
							}
						});

				final ListBoxField ptu = new ListBoxField("Device",
						new NotNullValidator());
				fieldset.add(ptu);

				interventionService.getDevices(true,
						new AsyncCallback<List<Device>>() {

							@Override
							public void onSuccess(List<Device> result) {
								for (Iterator<Device> i = result.iterator(); i
										.hasNext();) {
									Device device = i.next();
									devices.put(device.getId(), device);
									ptu.addItem(device.getName(),
											device.getId());
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								// log.warn("Caught : " + caught);
							}
						});

				final TextBoxField impact = new TextBoxField("Impact Number");
				fieldset.add(impact);

				final TextAreaField description = new TextAreaField(
						"Description");
				fieldset.add(description);

				final CheckBoxField test = new CheckBoxField(
						"Test Intervention");
				fieldset.add(test);

				final Modal m = new Modal();

				Button cancel = new Button("Cancel");
				cancel.setDismiss(ButtonDismiss.MODAL);
				cancel.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						m.hide();
					}
				});

				Button ok = new Button("Ok");
				ok.setDismiss(ButtonDismiss.MODAL);
				ok.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						m.hide();

						Intervention intervention = new Intervention(users
								.get(userField.getId()), devices.get(ptu
								.getId()), new Date(), impact.getValue(), 0.0,
								description.getValue(), test.getValue());

						interventionService.addIntervention(intervention,
								new AsyncCallback<Intervention>() {

									@Override
									public void onSuccess(Intervention intervention) {
									//	Window.alert("IID: "+intervention.getId());
										
										videoService.startVideo(intervention, new AsyncCallback<Void>() {
											
											@Override
											public void onSuccess(Void result) {
												// ignored
											}
											
											@Override
											public void onFailure(Throwable caught) {
												Window.alert(caught.getMessage());
											}
										});

										scheduler.update();
									}

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());
									}
								});						
					}
					// }
				});

				ValidationForm form = new ValidationForm(ok, cancel);
				form.setType(FormType.HORIZONTAL);
				form.add(fieldset);

				m.setTitle("New Intervention");
				m.add(form);
				ModalFooter footer = new ModalFooter();
				footer.add(cancel);
				footer.add(ok);
				m.add(footer);
				m.show();
			}
		});

		ClickableTextColumn<Intervention> duration = new ClickableTextColumn<Intervention>() {

			@Override
			public String getValue(Intervention object) {
				long d = object.getEndTime() == null ? new Date().getTime()
						: object.getEndTime().getTime();
				d = d - object.getStartTime().getTime();

				return HumanTime.upToMins(d);
			}
		};
		duration.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		duration.setSortable(false);
		if (selectable) {
			duration.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		table.addColumn(duration, new TextHeader("Duration"),
				clientFactory.isSupervisor() ? interventionFooter : null);

		// Name
		ClickableHtmlColumn<Intervention> name = new ClickableHtmlColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getName();
			}
		};
		name.setDataStoreName("user.lastName");
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(sortable);
		if (selectable) {
			name.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		Header<String> nameFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return "New User...";
			}

			public boolean onPreviewColumnSortEvent(Context context,
					Element elem, NativeEvent event) {
				// events are handled, do not sort, fix for #454
				return false;
			}
		};
		nameFooter.setUpdater(new ValueUpdater<String>() {

			@Override
			public void update(String value) {

				ValidationFieldset fieldset = new ValidationFieldset();

				final TextBoxField fname = new TextBoxField("First Name",
						new StringValidator(1, 50, "*"));
				fieldset.add(fname);

				final TextBoxField lname = new TextBoxField("Last Name",
						new StringValidator(2, 50, "*"));
				fieldset.add(lname);

				final TextBoxField cernId = new TextBoxField("CERN ID",
						new OrValidator<String>(new EmptyStringValidator(),
								new IntegerValidator("Enter a number")));
				fieldset.add(cernId);

				final Modal m = new Modal();

				final Button cancel = new Button("Cancel");
				cancel.setDismiss(ButtonDismiss.MODAL);
				cancel.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						m.hide();
					}
				});

				final Button ok = new Button("Ok");
				ok.setDismiss(ButtonDismiss.MODAL);
				ok.setEnabled(false);
				ok.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						m.hide();

						User user = new User(fname.getValue(),
								lname.getValue(), cernId.getValue());

						interventionService.addUser(user,
								new AsyncCallback<User>() {

									@Override
									public void onSuccess(User result) {
										scheduler.update();
									}

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());
									}
								});
					}
				});

				ValidationForm form = new ValidationForm(ok, cancel);
				form.setType(FormType.HORIZONTAL);
				form.add(fieldset);
				m.setTitle("New User");
				m.add(form);
				ModalFooter footer = new ModalFooter();
				footer.add(cancel);
				footer.add(ok);
				m.add(footer);
				m.show();
			}
		});
		table.addColumn(name, new TextHeader("Name"),
				clientFactory.isSupervisor() ? nameFooter : null);

		// PtuID
		ClickableTextColumn<Intervention> ptu = new ClickableTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getPtuId();
			}
		};
		ptu.setDataStoreName("device.name");
		ptu.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		ptu.setSortable(sortable);
		if (selectable) {
			ptu.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		Header<String> deviceFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return "New Device...";
			}

			public boolean onPreviewColumnSortEvent(Context context,
					Element elem, NativeEvent event) {
				// events are handled, do not sort, fix for #454
				return false;
			}
		};
		deviceFooter.setUpdater(new ValueUpdater<String>() {

			@Override
			public void update(String value) {
				ValidationFieldset fieldset = new ValidationFieldset();

				final TextBoxField ptuId = new TextBoxField("Device Name",
						new StringValidator(2, 20, "Enter alphanumeric ID"));
				final TextBoxField ip = new TextBoxField("IP");
				final String macAddressFormat = "XX:XX:XX:XX:XX:XX";
				final TextBoxField macAddress = new TextBoxField("MAC Address",
						new StringValidator(macAddressFormat.length(),
								macAddressFormat.length(), "Enter "
										+ macAddressFormat));
				final TextBoxField hostName = new TextBoxField("Host Name",
						new StringValidator(3, 50, "Enter valid hostname"));
				final TextAreaField description = new TextAreaField(
						"Description");
				fieldset.add(description);

				final CheckBoxField virtual = new CheckBoxField(
						"Virtual Device");
				fieldset.add(virtual);

				final Modal m = new Modal();

				Button cancel = new Button("Cancel");
				cancel.setDismiss(ButtonDismiss.MODAL);
				cancel.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						m.hide();
					}
				});

				Button ok = new Button("Ok");
				ok.setDismiss(ButtonDismiss.MODAL);
				ok.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						m.hide();
						
						InetAddress inetAddress = null;
						try {
							inetAddress = InetAddress.getByName(ip.getValue());
						} catch (IllegalArgumentException e) {
							inetAddress = InetAddress.getLocalHost();
						}

						Device device = new Device(ptuId.getValue(),
								inetAddress,
								description.getValue(), new MacAddress(
										macAddress.getValue()), hostName
										.getValue(), virtual.getValue());

						interventionService.addDevice(device,
								new AsyncCallback<Device>() {

									@Override
									public void onSuccess(Device result) {
										scheduler.update();
									}

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());
									}
								});
					}
					// }
				});

				ValidationFieldset fieldSet = new ValidationFieldset();
				fieldSet.add(ptuId);
				fieldSet.add(ip);
				fieldSet.add(macAddress);
				fieldSet.add(hostName);
				fieldSet.add(description);

				ValidationForm form = new ValidationForm(ok, cancel);
				form.setType(FormType.HORIZONTAL);
				form.add(fieldSet);

				m.setBackdrop(ModalBackdrop.TRUE);
				m.setFade(true);
				m.setKeyboard(true);
				m.setClosable(true);
				m.setTitle("New Device");
				m.add(form);
				
				ModalFooter footer = new ModalFooter();
				footer.add(cancel);
				footer.add(ok);
				m.add(footer);
				m.show();
			}
		});
		table.addColumn(ptu, new TextHeader("Device"),
				clientFactory.isSupervisor() ? deviceFooter : null);

		// Impact #
		EditTextColumn<Intervention> impact = new EditTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getImpactNumber() != null ? object
						.getImpactNumber() : "";
			}
		};
		impact.setDataStoreName("impactNumber");
		impact.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		impact.setSortable(true);
		impact.setEnabled(clientFactory.isSupervisor());
		impact.setFieldUpdater(new FieldUpdater<Intervention, String>() {
			@Override
			public void update(int index, Intervention intervention,
					String value) {
				if (!clientFactory.isSupervisor()) {
					return;
				}

				intervention.setImpactNumber(value);
				interventionService.updateIntervention(intervention,
						new AsyncCallback<Intervention>() {

							@Override
							public void onSuccess(Intervention result) {
								scheduler.update();
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}
						});
			}
		});
		table.addColumn(impact, new TextHeader("Impact #"), new TextHeader(""));

		// Rec Status
		// TextColumn<Intervention> recStatus = new TextColumn<Intervention>() {
		// @Override
		// public String getValue(Intervention object) {
		// return object.getRecStatus() != null ? Double.toString(object
		// .getRecStatus()) : "";
		// }
		// };
		// recStatus.setDataStoreName("recStatus");
		// recStatus.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		// recStatus.setSortable(true);
		// recStatus.setEnabled(clientFactory.isSupervisor());
		// table.addColumn(recStatus, new TextHeader("Rec Status"),
		// new TextHeader(""));

		final CheckboxColumn<Intervention> test = new CheckboxColumn<Intervention>() {
			@Override
			public Boolean getValue(Intervention intervention) {
				return intervention.isTest();
			}
		};
		test.setDataStoreName("test");
		test.setFieldUpdater(new FieldUpdater<Intervention, Boolean>() {

			@Override
			public void update(int index, Intervention intervention,
					Boolean value) {
				if (!clientFactory.isSupervisor()) {
					return;
				}

				intervention.setTest(value);
				interventionService.updateIntervention(intervention,
						new AsyncCallback<Intervention>() {

							@Override
							public void onSuccess(Intervention result) {
								scheduler.update();
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}
						});
			}
		});
		test.setEnabled(clientFactory.isSupervisor());
		test.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		test.setSortable(true);
		final TextHeader testHeader = new TextHeader("Test");
		final TextHeader testFooter = new TextHeader("");

		// Description
		EditTextColumn<Intervention> description = new EditTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getDescription() != null ? object
						.getDescription() : "";
			}
		};
		description.setDataStoreName("description");
		description.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		description.setSortable(true);
		description.setEnabled(clientFactory.isSupervisor());
		description.setFieldUpdater(new FieldUpdater<Intervention, String>() {
			@Override
			public void update(int index, Intervention intervention,
					String value) {
				if (!clientFactory.isSupervisor()) {
					return;
				}

				intervention.setDescription(value);
				interventionService.updateIntervention(intervention,
						new AsyncCallback<Intervention>() {

							@Override
							public void onSuccess(Intervention result) {
								scheduler.update();
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}
						});
			}
		});

		Header<String> descriptionFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return showTest ? "Hide Test" : "Show Test";
			}

			public boolean onPreviewColumnSortEvent(Context context,
					Element elem, NativeEvent event) {
				// events are handled, do not sort, fix for #454
				return false;
			}
		};

		descriptionFooter.setUpdater(new ValueUpdater<String>() {
			@Override
			public void update(String value) {

				showTest = value.toLowerCase().startsWith("show");
				localStorage
						.put(LocalStorage.SHOW_TEST_INTERVENTIONS, showTest);

				String style = showTest ? null : "hide";
				test.setCellStyleNames(style);
				testHeader.setHeaderStyleNames(style);
				testFooter.setHeaderStyleNames(style);

				InterventionView.this.update();
			}
		});

		table.addColumn(description, new TextHeader("Description"),
				descriptionFooter);
		table.addColumn(test, testHeader, testFooter);

		// Selection
		if (selectable) {
			final SingleSelectionModel<Intervention> selectionModel = new SingleSelectionModel<Intervention>();
			table.setSelectionModel(selectionModel);
			selectionModel
					.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

						@Override
						public void onSelectionChange(SelectionChangeEvent event) {
							Intervention m = selectionModel.getSelectedObject();
							// log.info(m + " " + event.getSource());
						}
					});
		}

		// Table
		dataProvider.addDataDisplay(table);

		// FIXME #189 this is the normal way, but does not work in our tabs...
		// tabs should detach, attach...
		addAttachHandler(new AttachEvent.Handler() {

			private Timer timer;

			@Override
			public void onAttachOrDetach(AttachEvent event) {

				if (event.isAttached()) {
					// refresh for duration
					timer = new Timer() {
						@Override
						public void run() {
							table.redraw();
						}
					};
					timer.scheduleRepeating(60000);
				} else {
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
				}
			}
		});

		// FIXME #189 so we handle it with events
		SelectTabEvent.subscribe(eventBus, new SelectTabEvent.Handler() {

			@Override
			public void onTabSelected(SelectTabEvent event) {
				if (event.getTab().equals("Interventions")) {
					showUpdate = true;
					table.redraw();
				}
			}
		});

		return true;
	}

	private void selectIntervention(Intervention intervention) {
	}

	private boolean needsUpdate() {
		if (showUpdate) {
			ColumnSortList sortList = table.getColumnSortList();
			ColumnSortInfo sortInfo = sortList.size() > 0 ? sortList.get(0)
					: null;
			if (sortInfo == null) {
				return true;
			}
			if (!sortInfo.getColumn().equals(endTime)) {
				return true;
			}
			if (sortInfo.isAscending()) {
				return true;
			}
			showUpdate = (scrollPanel.getVerticalScrollPosition() != scrollPanel
					.getMinimumVerticalScrollPosition())
					|| (pager.getPage() != pager.getPageStart());
			return showUpdate;
		}
		return false;
	}

	@Override
	public boolean update() {
		// show or hide update button
		updateButton.setVisible(needsUpdate());

		RangeChangeEvent.fire(table, table.getVisibleRange());
		table.redraw();

		return false;
	}
}
