package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.cas.singularws.SingularWebService;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawDropDownList;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.smallscreen.AdjustScreen;
import org.geogebra.common.euclidian.smallscreen.AdjustViews;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.euclidian3D.Input3DConstants;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.geogebra3D.util.CopyPaste3D;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.UndoManager.AppState;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoScriptRunner;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.sound.VideoManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.LowerCaseDictionary;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Represents an application window, gives access to views and system stuff
 */
@SuppressWarnings("javadoc")
public abstract class App implements UpdateSelection {
	/** Url for wiki article about functions */
	public static final String WIKI_OPERATORS = "Predefined Functions and Operators";
	/** Url for main page of manual */
	public static final String WIKI_MANUAL = "Manual";
	/** Url for wiki article about CAS */
	public static final String WIKI_CAS_VIEW = "CAS_View";
	/** Url for Intel RealSense tutorials */
	public static final String REALSENSE_TUTORIAL = "https://www.geogebra.org/m/OaGmb7LE";

	/** Url for wiki article about functions */
	public static final String WIKI_TEXT_TOOL = "Text Tool";
	/** id for dummy view */
	public static final int VIEW_NONE = 0;
	/** id for euclidian view */
	public static final int VIEW_EUCLIDIAN = 1;
	/** id for algebra view */
	public static final int VIEW_ALGEBRA = 2;
	/** id for Spreadsheet view */
	public static final int VIEW_SPREADSHEET = 4;
	/** id for CAS view */
	public static final int VIEW_CAS = 8;
	/** id for second euclidian view */
	public static final int VIEW_EUCLIDIAN2 = 16;
	/** id for construction protocol view */
	public static final int VIEW_CONSTRUCTION_PROTOCOL = 32;
	/** id for probability calculator view */
	public static final int VIEW_PROBABILITY_CALCULATOR = 64;
	/**
	 * id for data analysis view, ie multi/single/two variable analysisis tools
	 */
	public static final int VIEW_DATA_ANALYSIS = 70;
	/** id for function inspector */
	public static final int VIEW_FUNCTION_INSPECTOR = 128;
	/** id for 3D view */
	public static final int VIEW_EUCLIDIAN3D = 512;
	/** id for 2nd 3D view */
	public static final int VIEW_EUCLIDIAN3D_2 = 513;
	/** let us break the pattern */
	public static final int VIEW_EVENT_DISPATCHER = 42;
	/**
	 * id for view created from plane; also 1025 to 2047 might be used for this
	 * purpose
	 */
	public static final int VIEW_EUCLIDIAN_FOR_PLANE_START = 1024;
	/** maximal ID of view for plane */
	public static final int VIEW_EUCLIDIAN_FOR_PLANE_END = 2047;
	/**
	 * id for plot panels (small EVs eg in regression analysis tool)
	 */
	public static final int VIEW_PLOT_PANEL = 2048;
	/**
	 * id for text preview in text tool
	 */
	public static final int VIEW_TEXT_PREVIEW = 4096;
	/**
	 * id for properties view
	 */
	public static final int VIEW_PROPERTIES = 4097;
	// please let 1024 to 2047 empty
	/** id for spreadsheet table model */
	public static final int VIEW_TABLE_MODEL = 9000;
	/** data collection view (web only) */
	public static final int VIEW_DATA_COLLECTION = 43;
	public static final int DEFAULT_THRESHOLD = 3;
	public static final int DEFAULT_THRESHOLD_FACTOR_FOR_BLOB_IN_SLIDER = 3;
	/**
	 * minimal font size
	 */
	public static final int MIN_FONT_SIZE = 10;
	/**
	 * initial number of columns for spreadsheet
	 */
	public static final int SPREADSHEET_INI_COLS = 10;
	/**
	 * initial number of rows for spreadsheet
	 */
	public static final int SPREADSHEET_INI_ROWS = 100;
	// used by PropertyDialogGeoElement and MenuBarImpl
	// for the Rounding Menus
	/**
	 * Rounding menu options (not internationalized)
	 */
	final private static String[] strDecimalSpacesAC = { "0 decimals",
			"1 decimals", "2 decimals", "3 decimals", "4 decimals",
			"5 decimals", "10 decimals", "15 decimals", "", "3 figures",
			"5 figures", "10 figures", "15 figures" };

	/** Singular web service (CAS) */
	private SingularWebService singularWS;

	private static String CASVersionString = "";
	private static boolean CASViewEnabled = true;
	private static boolean _3DViewEnabled = true;
	/**
	 * whether axes should be visible when EV is created first element of this
	 * array is for x-axis, second for y-axis
	 */
	protected final boolean[] showAxes = { true, true };
	/** whether axes should be logarithmic when EV is created */
	protected final boolean[] logAxes = { false, false };
	/**
	 * Whether we are running applet in frame. Not possible with 4.2+ (we need
	 * this to hide reset icon from EV)
	 */
	public boolean runningInFrame = false;
	public Vector<GeoImage> images = new Vector<>();
	/**
	 * Whether AV should show auxiliary objects stored here rather than in
	 * algebra view so that it can be set without creating an AV (compatibility
	 * with 3.2)
	 */
	public boolean showAuxiliaryObjects = false;
	/** flag to test whether to draw Equations full resolution */
	public ExportType exportType = ExportType.NONE;
	/**
	 * right angle style
	 *
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_SQUARE
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_DOT
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_L
	 * @see EuclidianStyleConstants#RIGHT_ANGLE_STYLE_NONE
	 */
	public int rightAngleStyle = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
	private AlgoKimberlingWeightsInterface kimberlingw = null;
	private AlgoCubicSwitchInterface cubicw = null;
	/**
	 * whether transparent cursor should be used while dragging
	 */
	private boolean useTransparentCursorWhenDragging = false;
	/**
	 * Script manager
	 */
	protected ScriptManager scriptManager = null;
	/** whether current construction was saved after last changes */
	protected boolean isSaved = true;
	/**
	 * object is hit if mouse is within this many pixels (more for points, see
	 * geogebra.common.euclidian.DrawPoint)
	 */
	protected int capturingThreshold = DEFAULT_THRESHOLD;
	/** on touch devices we want larger threshold for point hit testing */
	protected int capturingThresholdTouch = 3 * DEFAULT_THRESHOLD;

	/* Font settings */
	/**
	 * where to show the inputBar (respective inputBox)
	 */
	protected InputPosition showInputTop = InputPosition.algebraView;
	/**
	 * Whether input bar should be visible
	 */
	protected boolean showAlgebraInput = true;

	// For eg Hebrew and Arabic.
	/**
	 * Whether toolbar help should appear
	 */
	protected boolean showToolBarHelp = false;
	/**
	 * Toolbar position
	 */
	protected int toolbarPosition = 1;
	/**
	 * Whether input help toggle button should be visible
	 */
	protected boolean showInputHelpToggle = true;
	/**
	 * whether righ click is enabled
	 */
	protected boolean rightClickEnabled = true;
	/**
	 * whether righ click is enabled for Algebra View
	 */
	protected boolean rightClickEnabledForAV = true;
	/**
	 * User Sign in handling
	 */
	protected LogInOperation loginOperation = null;
	/** XML input / output handler */
	private MyXMLio myXMLio;
	/** gui / menu fontsize (-1 = use appFontSize) */
	protected int guiFontSize = -1;
	/** kernel */
	protected Kernel kernel;
	/** whether points can be created by other tools than point tool */
	protected boolean isOnTheFlyPointCreationActive = true;
	/** Settings object */
	protected Settings settings;
	/** Selections in this app */
	protected SelectionManager selection;
	/** whether grid should be visible when EV is created */
	protected boolean showGrid = false;
	/**
	 * this flag is true during initialization phase (until GUI is built and
	 * command line args handled, incl. file loading) or when we are opening a
	 * file
	 */
	protected boolean initing = false;
	/** Euclidian view */
	protected EuclidianView euclidianView;
	/** Euclidian view's controller */
	protected EuclidianController euclidianController;
	/** selection listener */
	protected GeoElementSelectionListener currentSelectionListener;
	/**
	 * whether menubar should be visible
	 */
	protected boolean showMenuBar = true;
	protected String uniqueId;
	private ArrayList<Perspective> tmpPerspectives = new ArrayList<>();
	/**
	 * whether toolbar should be visible
	 */
	protected boolean showToolBar = true;
	/**
	 * whether shift, drag and zoom features are enabled
	 */
	protected boolean shiftDragZoomEnabled = true;
	protected int appletWidth = 0;
	protected int appletHeight = 0;
	protected boolean useFullGui = false;
	protected int appCanvasHeight;
	protected int appCanvasWidth;
	protected boolean needsSpreadsheetTableModel = false;
	protected HashMap<Integer, Boolean> showConstProtNavigationNeedsUpdate = null;
	protected HashMap<Integer, Boolean> showConsProtNavigation = null;
	protected AppCompanion companion;
	protected boolean prerelease;
	protected boolean canary;

	private boolean showResetIcon = false;
	private ParserFunctions pf;
	private SpreadsheetTraceManager traceManager;
	private ExamEnvironment exam;
	// currently used application fonts
	private int appFontSize = 16;
	// moved to Application from EuclidianView as the same value is used across
	// multiple EVs
	private int maxLayerUsed = 0;
	/**
	 * size of checkboxes, default in GeoGebraPreferencesXML.java
	 * checkboxSize="26"
	 */
	private int booleanSize = EuclidianConstants.DEFAULT_CHECKBOX_SIZE;
	private boolean labelDragsEnabled = true;
	private boolean undoRedoEnabled = true;

	// command dictionary
	private LowerCaseDictionary commandDict;
	private LowerCaseDictionary commandDictCAS;
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;
	private final Object commandDictLock = new Object();
	private String scriptingLanguage;
	/**
	 * flag for current state
	 */
	private CoordSystemStateForUndo storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.NONE;
	private boolean blockUpdateScripts = false;
	private boolean useBrowserForJavaScript = true;
	private EventDispatcher eventDispatcher;
	private int[] versionArray = null;
	private List<SavedStateListener> savedListeners = new ArrayList<>();
	private Macro macro;
	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;
	/**
	 * says that a labeling style is selected in menu (i.e. all default geos use
	 * the selected labeling style)
	 */
	private boolean labelingStyleSelected = true;
	private boolean scriptingDisabled = false;
	private double exportScale = 1;
	private PropertiesView propertiesView;
	private Random random = new Random();
	private GeoScriptRunner geoScriptRunner;
	private GeoElement geoForCopyStyle;
	private boolean isErrorDialogsActive = true;
	private ArrayList<OpenFileListener> openFileListener;
	// whether to allow perspective and login popups
	private boolean allowPopUps = false;

	private Versions version;
	/**
	 * static so that you can copy & paste between instances
	 */
	public static volatile CopyPaste copyPaste = null;
	static final protected long SCHEDULE_PREVIEW_DELAY_IN_MILLISECONDS = 100;

	private ArrayList<String> mLastCommandsSelectedFromHelp;
	// TODO: move following methods somewhere else
	private int tubeID = 0;
	private boolean isAutoSaved = true;
	private AdjustViews adjustViews = null;
	private AdjustScreen adjustScreen = null;
	private AdjustScreen adjustScreen2 = null;
	private long ceIDcounter = 1;
	private int nextVariableID = 1;
	private boolean buttonShadows = false;
	private double buttonRounding = 0.2;
	private SpecialPointsManager specialPointsManager;

	private boolean areCommands3DEnabled = true;
	private static volatile MD5EncrypterGWTImpl md5Encrypter;

	public static String[] getStrDecimalSpacesAC() {
		return strDecimalSpacesAC;
	}

	/**
	 * Please call setVersion right after this
	 */
	public App() {
		init();
	}

	/**
	 * constructor
	 */
	public App(Versions version) {
		this();
		this.version = version;
	}

	protected void init() {
		companion = newAppCompanion();
		resetUniqueId();
	}

	/**
	 * Changes version; should be called only once, right after the constructor
	 * 
	 * @param version
	 *            version
	 */
	public void setVersion(Versions version) {
		this.version = version;
	}

	/**
	 * @return CAS version
	 */
	public static final String getCASVersionString() {
		return CASVersionString;

	}

	/**
	 * @param string
	 *            CAS version string
	 */
	public static final void setCASVersionString(String string) {
		CASVersionString = string;

	}

	/**
	 * Initializes SingularWS
	 */
	public void initializeSingularWS() {
		singularWS = new SingularWebService();
		singularWS.enable();
		if (singularWS.isAvailable()) {
			Log.info("SingularWS is available at "
					+ singularWS.getConnectionSite());
			// debug(singularWS.directCommand("ring r=0,(x,y),dp;ideal
			// I=x^2,x;groebner(I);"));
		} else {
			Log.info("No SingularWS is available at "
					+ singularWS.getConnectionSite() + " (yet)");
		}
	}

	public SingularWebService getSingularWS() {
		return singularWS;
	}

	/* selection handling */

	/**
	 * @param version
	 *            string version, eg 4.9.38.0
	 * @return version as list of ints, eg [4,9,38,0]
	 */
	static final public int[] getSubValues(String version) {
		String[] values = version.split("\\.");
		int[] ret = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			ret[i] = Integer.parseInt(values[i]);
		}

		return ret;
	}

	/**
	 * Disables CAS View.
	 */
	public static void disableCASView() {
		CASViewEnabled = false;
	}

	// Rounding Menus end

	/**
	 * Disables 3D View.
	 */
	public static void disable3DView() {
		_3DViewEnabled = false;
	}

	/**
	 * Gets max scale based on EV size; scale down if EV too big to avoid
	 * clipboard errors
	 * 
	 * @param ev
	 *            view
	 * @return maximum scale for clipboard images; default 2
	 */
	public static double getMaxScaleForClipBoard(EuclidianView ev) {
		double size = ev.getExportWidth() * ev.getExportHeight();

		// Windows XP clipboard has trouble with images larger than this
		// at double scale (with scale = 2d)
		if (size > 500000) {
			return 2.0 * Math.sqrt(500000 / size);
		}

		return 2d;
	}

	/**
	 *
	 * @param id
	 *            view id
	 * @return true if id is a 3D view id
	 */
	public static final boolean isView3D(int id) {
		if (id == App.VIEW_EUCLIDIAN3D) {
			return true;
		}

		if (id == App.VIEW_EUCLIDIAN3D_2) {
			return true;
		}

		return false;

	}

	/**
	 * @return global checkbox size 13 or 26 (all checkboxes, both views)
	 */
	public int getCheckboxSize() {
		return booleanSize;
	}

	/**
	 *
	 * set global checkbox size (all checkboxes, both views)
	 *
	 * @param b
	 *            new size for checkboxes (either 13 or 26)
	 */
	public void setCheckboxSize(int b) {
		booleanSize = (b == 13) ? 13 : 26;
	}

	/**
	 * @param type
	 *            mouse or touch
	 * @return capturing threshold
	 */
	public int getCapturingThreshold(PointerEventType type) {
		return type == PointerEventType.TOUCH ? this.capturingThresholdTouch
				: this.capturingThreshold;
	}

	/**
	 * @param i
	 *            capturing threshold
	 */
	public void setCapturingThreshold(int i) {
		this.capturingThreshold = i;
		this.capturingThresholdTouch = 3 * i;
	}

	/**
	 * We need this method so that we can override it using more powerful
	 * normalizer
	 *
	 * @return new lowercase dictionary
	 */
	public LowerCaseDictionary newLowerCaseDictionary() {
		return new LowerCaseDictionary(new NormalizerMinimal());
	}

	/**
	 * Fills CAS command dictionary and translation table. Must be called before
	 * we start using CAS view.
	 */
	public void fillCasCommandDict() {
		// this method might get called during initialization, when we're not
		// yet
		// ready to fill the casCommandDict. In that case, we will fill the
		// dict during fillCommandDict :)

		if (!getLocalization().isCommandChanged() && ((commandDictCAS != null)
				|| getLocalization().isCommandNull())) {
			return;
		}
		GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
		if (cas == null || subCommandDict == null) {
			return;
		}
		getLocalization().setCommandChanged(false);

		commandDictCAS = newLowerCaseDictionary();
		subCommandDict[CommandsConstants.TABLE_CAS].clear();

		// get all commands from the commandDict and write them to the
		// commandDictCAS

		// the keySet contains all commands of the dictionary; see
		// LowerCaseDictionary.addEntry(String s) for more
		Collection<String> commandDictContent = commandDict.values();

		// write them to the commandDictCAS
		for (String cmd : commandDictContent) {
			commandDictCAS.addEntry(cmd);
		}

		// iterate through all available CAS commands, add them (translated if
		// available, otherwise untranslated)
		for (String cmd : cas.getAvailableCommandNames()) {

			try {
				String local = getLocalization().getCommand(cmd);
				putInTranslateCommandTable(Commands.valueOf(cmd), local);
				if (local != null) {
					commandDictCAS.addEntry(local);
					subCommandDict[CommandsConstants.TABLE_CAS].addEntry(local);
				} else {
					commandDictCAS.addEntry(cmd);
					subCommandDict[CommandsConstants.TABLE_CAS].addEntry(cmd);
				}
			} catch (Exception mre) {
				commandDictCAS.addEntry(cmd);
				subCommandDict[CommandsConstants.TABLE_CAS].addEntry(cmd);
			}
		}
	}

	/**
	 * @return command dictionary for CAS
	 */
	public final LowerCaseDictionary getCommandDictionaryCAS() {
		synchronized (commandDictLock) {
			fillCommandDict();
			fillCasCommandDict();
		}
		return commandDictCAS;
	}

	/**
	 * Returns an array of command dictionaries corresponding to the categorized
	 * sub command sets created in CommandDispatcher.
	 *
	 * @return command dictionaries corresponding to the categories
	 */
	public final LowerCaseDictionary[] getSubCommandDictionary() {

		if (subCommandDict == null) {
			initTranslatedCommands();
		}
		if (getLocalization().isCommandChanged()) {
			updateCommandDictionary();
		}

		return subCommandDict;
	}

	/**
	 * Initializes the translated command names for this application. Note: this
	 * will load the properties files first.
	 */
	final public void initTranslatedCommands() {
		synchronized (commandDictLock) {
			if (getLocalization().isCommandNull() || subCommandDict == null) {
				getLocalization().initCommand();
				fillCommandDict();
				kernel.updateLocalAxesNames();
			}
		}
	}

	/**
	 * @return command dictionary
	 */
	public final LowerCaseDictionary getCommandDictionary() {
		synchronized (commandDictLock) {
			fillCommandDict();
		}
		return commandDict;
	}

	/**
	 * Fill command dictionary and translation table. Must be called before we
	 * start using Input Bar.
	 */
	protected void fillCommandDict() {
		getLocalization().initCommand();
		if (!getLocalization().isCommandChanged()) {
			return;
		}
		// translation table for all command names in command.properties
		getLocalization().initTranslateCommand();
		// command dictionary for all public command names available in
		// GeoGebra's input field
		// removed check for null: commandDict.clear() removes keys, but they
		// are still available with commandDict.getIterator()
		// so change English -> French -> English doesn't work in the input bar
		// see AutoCompleteTextfield.lookup()
		// if (commandDict == null)
		commandDict = newLowerCaseDictionary();
		// else commandDict.clear();

		// =====================================
		// init sub command dictionaries

		if (subCommandDict == null) {
			subCommandDict = new LowerCaseDictionary[CommandDispatcher.tableCount];
			for (int i = 0; i < subCommandDict.length; i++) {
				subCommandDict[i] = newLowerCaseDictionary();
			}
		}
		for (int i = 0; i < subCommandDict.length; i++) {
			subCommandDict[i].clear();
			// =====================================
		}
		HashMap<String, String> translateCommandTable = getLocalization()
				.getTranslateCommandTable();
		for (Commands comm : Commands.values()) {
			String internal = comm.name();
			if (!companion.tableVisible(comm.getTable())
					|| !kernel.getAlgebraProcessor().isCommandsEnabled()) {
				if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
					putInTranslateCommandTable(comm, null);
				}
				continue;
			}

			// Log.debug(internal);
			String local = getLocalization().getCommand(internal);
			putInTranslateCommandTable(comm, local);

			if (local != null) {
				local = local.trim();
				// case is ignored in translating local command names to
				// internal names!
				translateCommandTable.put(StringUtil.toLowerCaseUS(local),
						internal);

				commandDict.addEntry(local);
				// add public commands to the sub-command dictionaries
				subCommandDict[comm.getTable()].addEntry(local);

			}

		}
		getParserFunctions().updateLocale(getLocalization());
		// get CAS Commands
		if (kernel.isGeoGebraCASready()) {
			fillCasCommandDict();
		}
		addMacroCommands();
		getLocalization().setCommandChanged(false);
	}

	private void putInTranslateCommandTable(Commands comm, String local) {
		String internal = comm.name();
		// Check that we don't overwrite local with English
		HashMap<String, String> translateCommandTable = getLocalization()
				.getTranslateCommandTable();
		if (!translateCommandTable
				.containsKey(StringUtil.toLowerCaseUS(internal))) {
			translateCommandTable.put(StringUtil.toLowerCaseUS(internal),
					Commands.englishToInternal(comm).name());
		}
		if (comm.getTable() == CommandsConstants.TABLE_ENGLISH) {
			return;
		}

		if (local != null) {
			translateCommandTable.put(StringUtil.toLowerCaseUS(local),
					Commands.englishToInternal(comm).name());
		}

	}

	/**
	 * translate command name to internal name. Note: the case of localname is
	 * NOT relevant
	 *
	 * @param command
	 *            local name
	 * @return internal name
	 */
	public String getReverseCommand(String command) {
		// don't init command table on file loading
		if (kernel.isUsingInternalCommandNames()) {
			try {
				Commands.valueOf(command);
				return command;
			} catch (Exception e) {
				// not a valid command, fall through
			}
		}
		initTranslatedCommands();

		return getLocalization().getReverseCommand(command);
	}

	/**
	 * Updates command dictionary
	 */
	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		synchronized (commandDictLock) {
			if (commandDict != null) {
				fillCommandDict();
			}
		}
	}

	/**
	 * Adds macro commands to the dictionary
	 */
	protected void addMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<Macro> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			if (!commandDict.containsValue(cmdName)) {
				commandDict.addEntry(cmdName);
			}
		}
	}

	/**
	 * Remove macros from command dictionary
	 */
	public void removeMacroCommands() {
		if ((commandDict == null) || (kernel == null) || !kernel.hasMacros()) {
			return;
		}

		ArrayList<Macro> macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = macros.get(i).getCommandName();
			commandDict.removeEntry(cmdName);
		}
	}

	public abstract boolean isApplet();

	/**
	 * Store current state of construction for undo/redo purposes
	 */
	public abstract void storeUndoInfo();

	/**
	 * Store current state of construction for undo/redo purposes, and state of
	 * construction for mode starting (so undo cancels partial tool preview)
	 */
	public void storeUndoInfoAndStateForModeStarting() {
		storeUndoInfoAndStateForModeStarting(true);
	}

	/**
	 * Store global undo point an possibly one for mode.
	 * 
	 * @param storeForMode
	 *            whether to store a mode undo point too
	 */
	final public void storeUndoInfoAndStateForModeStarting(
			boolean storeForMode) {
		if (isUndoActive()) {
			if (storeForMode) {
				kernel.storeUndoInfoAndStateForModeStarting();
			} else {
				kernel.storeUndoInfo();
			}
			setUnsaved();
		}
	}

	/**
	 * store undo info only if view coord system has changed
	 */
	public void storeUndoInfoIfSetCoordSystemOccured() {
		if (storeUndoInfoForSetCoordSystem == CoordSystemStateForUndo.SET_COORD_SYSTEM_OCCURED) {
			storeUndoInfo();
		}

		storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.NONE;
	}

	/**
	 * tells the application that a view coord system has changed
	 */
	public void setCoordSystemOccured() {

		if (storeUndoInfoForSetCoordSystem == CoordSystemStateForUndo.MAY_SET_COORD_SYSTEM) {
			storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.SET_COORD_SYSTEM_OCCURED;
		}
	}

	/**
	 * tells the coord sys may be set
	 */
	public void maySetCoordSystem() {
		if (storeUndoInfoForSetCoordSystem == CoordSystemStateForUndo.NONE) {
			storeUndoInfoForSetCoordSystem = CoordSystemStateForUndo.MAY_SET_COORD_SYSTEM;
		}
	}

	public void setPropertiesOccured() {
		getKernel().getConstruction().getUndoManager().setPropertiesOccured();
	}

	public void storeUndoInfoForProperties() {
		getKernel().getConstruction().getUndoManager()
				.storeUndoInfoForProperties(isUndoActive());
	}

	/**
	 * @return true if we have access to complete gui (menubar, toolbar); false
	 *         for minimal applets (just one EV, no gui)
	 */
	public abstract boolean isUsingFullGui();

	/**
	 *
	 * @param view
	 *            view ID
	 * @return whether view with given ID is visible
	 */
	public abstract boolean showView(int view);

	public boolean letRename() {
		return true;
	}

	public boolean letDelete() {
		return true;
	}

	public boolean letRedefine() {
		return true;
	}

	/**
	 * @return the blockUpdateScripts
	 */
	public boolean isBlockUpdateScripts() {
		return blockUpdateScripts;
	}

	/**
	 * @param blockUpdateScripts
	 *            the blockUpdateScripts to set
	 */
	public void setBlockUpdateScripts(boolean blockUpdateScripts) {
		this.blockUpdateScripts = blockUpdateScripts;
	}

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 *
	 * @param cmd
	 *            localized command name
	 * @return internal command name
	 */
	public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCaseUS(cmd);
		Commands[] values = Commands.values();
		for (Commands c : values) {
			s = Commands.englishToInternal(c).name();

			// make sure that when si[] is typed in script, it's changed to
			// Si[] etc
			if (StringUtil.toLowerCaseUS(getLocalization().getCommand(s))
					.equals(cmdLower)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Translate key and then show error dialog
	 *
	 * @param key
	 *            error message
	 */
	public void localizeAndShowError(String key) {
		showError(getLocalization().getError(key));
	}

	/**
	 * Show error dialog with given text
	 *
	 * @param localizedError
	 *            error message
	 */
	public abstract void showError(String localizedError);

	/**
	 * Shows error dialog with a given text
	 *
	 * @param msg
	 *            error message
	 */
	protected abstract void showErrorDialog(String msg);

	/**
	 * @param useBrowserForJavaScript
	 *            desktop: determines whether Rhino will be used (false) or the
	 *            browser (true) web: determines whether JS input comes from the
	 *            html file (true) or from the ggb file (false)
	 */
	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	/**
	 * @return desktop: determines whether Rhino will be used (false) or the
	 *         browser (true) web: determines whether JS input comes from the
	 *         html file (true) or from the ggb file (false)
	 */
	public boolean useBrowserForJavaScript() {
		return useBrowserForJavaScript;
	}

	/**
	 * @return script manager
	 */
	final public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = newScriptManager();
		}
		return scriptManager;
	}

	abstract protected ScriptManager newScriptManager();

	/**
	 * Get the event dispatcher, which dispatches events objects that manage
	 * event driven scripts
	 *
	 * @return the app's event dispatcher
	 */
	public EventDispatcher getEventDispatcher() {
		if (eventDispatcher == null) {
			eventDispatcher = new EventDispatcher(this);
		}
		return eventDispatcher;
	}

	/**
	 * @param ge
	 *            geo
	 * @return trace-related XML elements
	 */
	final public String getTraceXML(GeoElement ge) {
		return getTraceManager().getTraceXML(ge);
	}

	/**
	 * Start tracing geo to spreadsheet
	 *
	 * @param ge
	 *            geo
	 */
	public void traceToSpreadsheet(GeoElement ge) {
		getTraceManager().traceToSpreadsheet(ge);
	}

	/**
	 * Reset tracing column for given geo
	 *
	 * @param ge
	 *            geo
	 */
	public void resetTraceColumn(GeoElement ge) {
		getTraceManager().setNeedsColumnReset(ge, true);
	}

	/**
	 * Updates the counter of used layers
	 *
	 * @param layer
	 *            layer to which last element was added
	 */
	public void updateMaxLayerUsed(int layer) {
		int newLayer = layer;
		if (layer > EuclidianStyleConstants.MAX_LAYERS) {
			newLayer = EuclidianStyleConstants.MAX_LAYERS;
		}
		if (layer > maxLayerUsed) {
			maxLayerUsed = newLayer;
		}
	}

	/**
	 * @return whether this is a 3D app or not
	 */
	public boolean is3D() {
		return false;
	}

	/**
	 * @return last created GeoElement
	 */
	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}

	/**
	 * Deletes selected objects
	 */
	public void deleteSelectedObjects(boolean isCut) {
		if (letDelete()) {
			Object[] geos = selection.getSelectedGeos().toArray();
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isProtected(EventType.REMOVE)) {
					if (isCut || geo.isShape()) {
						if (geo.getParentAlgorithm() != null) {
							for (GeoElement ge : geo
									.getParentAlgorithm().input) {
								ge.removeOrSetUndefinedIfHasFixedDescendent();
							}
						}
					}
					geo.removeOrSetUndefinedIfHasFixedDescendent();
				}
			}

			// also delete just created geos if possible
			ArrayList<GeoElement> geos2 = getActiveEuclidianView()
					.getEuclidianController().getJustCreatedGeos();
			for (int j = 0; j < geos2.size(); j++) {
				GeoElement geo = geos2.get(j);
				if (!geo.isProtected(EventType.REMOVE)) {
					geo.removeOrSetUndefinedIfHasFixedDescendent();
				}
			}
			getActiveEuclidianView().getEuclidianController()
					.clearJustCreatedGeos();
			getActiveEuclidianView().getEuclidianController()
					.clearSelectionAndRectangle();
			storeUndoInfoAndStateForModeStarting();
		}
	}

	public ArrayList<GeoElement> getJustCreatedGeos() {
		return getActiveEuclidianView().getEuclidianController()
				.getJustCreatedGeos();
	}

	/**
	 * @return whether auxiliary objects are shown in AV
	 */
	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}

	/**
	 * Append XML describing the keyboard to given string builder
	 *
	 * @param sb
	 *            string builder
	 */
	public void getKeyboardXML(StringBuilder sb) {
		sb.append("<keyboard width=\"");
		sb.append(getSettings().getKeyboard().getKeyboardWidth());
		sb.append("\" height=\"");
		sb.append(getSettings().getKeyboard().getKeyboardHeight());
		sb.append("\" opacity=\"");
		sb.append(getSettings().getKeyboard().getKeyboardOpacity());
		sb.append("\" language=\"");
		sb.append(getSettings().getKeyboard().getKeyboardLocale());
		sb.append("\" show=\"");
		sb.append(getSettings().getKeyboard().isShowKeyboardOnStart());
		sb.append("\"/>");
	}

	/**
	 * @return true if we have critically low free memory
	 */
	public abstract boolean freeMemoryIsCritical();

	/**
	 * @return Approximate amount of remaining memory in bytes
	 */
	public abstract long freeMemory();

	/**
	 * set right angle style
	 *
	 * @param style
	 *            style
	 */
	public void setRightAngleStyle(int style) {
		rightAngleStyle = style;
	}

	/**
	 * @return the maximal currently used layer
	 */
	public int getMaxLayerUsed() {
		return maxLayerUsed;
	}

	/**
	 * @param min
	 *            real world x min
	 * @param max
	 *            real world x max
	 * @return number of pixels in EV1 between given x coordinates
	 */
	public double countPixels(double min, double max) {
		EuclidianView ev = getEuclidianView1();
		return ev.toScreenCoordXd(max) - ev.toScreenCoordXd(min);
	}

	/**
	 * @return algebra view
	 */
	public abstract AlgebraView getAlgebraView();

	/**
	 * @return EV1
	 */
	public EuclidianView getEuclidianView1() {
		return euclidianView;
	}

	/**
	 * Resets the maximal used llayer to 0
	 */
	public void resetMaxLayerUsed() {
		maxLayerUsed = 0;
	}

	/**
	 * @return active euclidian view (may be EV, EV2 or 3D)
	 */
	public abstract EuclidianView getActiveEuclidianView();

	/**
	 * @return whether 3D view was initialized
	 */
	public boolean isEuclidianView3Dinited() {
		return false;
	}

	/**
	 * @return 3D view
	 */
	public EuclidianView3DInterface getEuclidianView3D() {
		return null;
	}

	/**
	 * @return whether EV2 was initialized
	 */
	public abstract boolean hasEuclidianView2EitherShowingOrNot(int idx);

	/**
	 * @return whether EV2 is visible
	 */
	public abstract boolean isShowingEuclidianView2(int idx);

	/**
	 * @return image manager
	 */
	public abstract ImageManager getImageManager();

	/**
	 * @return gui manager (it's null in minimal applets)
	 */
	public abstract GuiManagerInterface getGuiManager();

	/**
	 * @return dialog manager
	 */
	public abstract DialogManager getDialogManager();

	/**
	 * Initializes GUI manager
	 */
	protected abstract void initGuiManager();

	/**
	 * Whether we are running on Mac
	 *
	 * @return whether we are running on Mac
	 */
	public boolean isMacOS() {
		return false;
	}

	/**
	 * Whether we are running on Windows
	 *
	 * @return whether we are running on Windows
	 */
	public boolean isWindows() {
		return false;
	}

	/**
	 * @return the scriptingLanguage
	 */
	public String getScriptingLanguage() {
		// in some files we stored language="null" accidentally
		if ("null".equals(scriptingLanguage)) {
			scriptingLanguage = null;
		}
		return scriptingLanguage;
	}

	/**
	 * @param scriptingLanguage
	 *            the scriptingLanguage to set
	 */
	public void setScriptingLanguage(String scriptingLanguage) {
		this.scriptingLanguage = scriptingLanguage;
	}

	/**
	 * Runs JavaScript
	 *
	 * @param app
	 *            application
	 * @param script
	 *            JS method name
	 * @param arg
	 *            arguments
	 * @throws Exception
	 *             when script contains errors
	 */
	public abstract void evalJavaScript(App app, String script, String arg)
			throws Exception;

	/**
	 * @param v
	 *            version parts
	 * @return whether given version is newer than this code
	 */
	public boolean fileVersionBefore(int[] v) {
		if (this.versionArray == null) {
			return true;
		}

		int length = versionArray.length;
		if (v.length < length) {
			length = v.length;
		}

		for (int i = 0; i < length; i++) {
			if (versionArray[i] < v[i]) {
				return true;
			} else if (versionArray[i] > v[i]) {
				return false;
			}
		}

		return versionArray.length < v.length;
	}

	/**
	 * Sets version of currently loaded file
	 *
	 * @param version
	 *            version string
	 * @param appName
	 *            name of the app that saved current file
	 */
	public void setFileVersion(String version, String appName) {
		if (version == null) {
			this.versionArray = null;
			return;
		}

		this.versionArray = getSubValues(version);
	}

	/**
	 * @return euclidian view; if not present yet, new one is created
	 */
	public abstract EuclidianView createEuclidianView();

	/**
	 * Returns current mode (tool number)
	 *
	 * @return current mode
	 */
	final public int getMode() {
		EuclidianView view = getActiveEuclidianView();
		if (view == null) {
			view = getEuclidianView1();
		}
		return view.getMode();
	}

	public void setMode(int mode) {
		setMode(mode, ModeSetter.TOOLBAR);
	}

	/**
	 * Returns labeling style for newly created geos
	 *
	 * @return labeling style; AUTOMATIC is resolved either to
	 *         USE_DEFAULTS/POINTS_ONLY (for 3D) or OFF depending on visibility
	 *         of AV
	 */
	public int getCurrentLabelingStyle() {
		if (getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {

			if ((getGuiManager() != null)
					&& getGuiManager().hasAlgebraViewShowing()) {
				if (getAlgebraView().isVisible()) {
					if (isView3D(getGuiManager().getLayout().getDockManager()
							.getFocusedViewId())) {
						// only points (and sliders and angles) are labeled
						// for 3D
						return ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;
					}
					// default behaviour for other views
					return ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS;
				}
				// no AV: no label
				return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;
			}
			return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF;

		}
		return getLabelingStyle();
	}

	/**
	 * This is needed for handling paths to images inside .ggb archive TODO
	 * probably we should replace this methodby something else as images are
	 * different in web
	 *
	 * @param fullPath
	 *            path to image
	 * @return legth of MD5 hash output
	 */
	public int getMD5folderLength(String fullPath) {
		return 32;
	}

	/**
	 * @param filename
	 *            filename
	 * @return image wrapped in GBufferedImage
	 */
	public abstract MyImage getExternalImageAdapter(String filename, int width,
			int height);

	/**
	 * @return whether label dragging is enableded
	 */
	final public boolean isLabelDragsEnabled() {
		return labelDragsEnabled;
	}

	/**
	 * Enables or disables label dragging in this application. This is useful
	 * for applets.
	 *
	 * @param flag
	 *            true to allow label dragging
	 */
	public void setLabelDragsEnabled(boolean flag) {
		labelDragsEnabled = flag;
	}

	/**
	 * Enables or disables undo/redo in this application. This is useful for
	 * applets.
	 *
	 * @param flag
	 *            true to allow Undo / Redo
	 */
	public void setUndoRedoEnabled(boolean flag) {
		undoRedoEnabled = flag;
		if (!undoRedoEnabled && kernel != null) {
			kernel.setUndoActive(false);
		}
	}

	/**
	 * @return whether undo / redo are possible
	 */
	public boolean isUndoRedoEnabled() {
		return undoRedoEnabled;
	}

	/**
	 * Enable / disable autoscroll in spreadsheet.
	 * 
	 * @param scrollToShow
	 *            scrolling flag for spreadsheet
	 */
	public final void setScrollToShow(boolean scrollToShow) {
		if (getGuiManager() != null) {
			getGuiManager().setScrollToShow(scrollToShow);
		}
	}

	/**
	 * Sets state of application to "saved", so that no warning appears on
	 * close.
	 *
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 */
	public void setSaved() {
		isSaved = true;
		for (SavedStateListener sl : savedListeners) {
			sl.stateChanged(true);
		}
	}

	public void registerSavedStateListener(SavedStateListener l) {
		savedListeners.add(l);
	}

	/**
	 * Sets application state to "unsaved" so that user is reminded on close.
	 */
	public void setUnsaved() {
		isSaved = false;
		isAutoSaved = false;
		for (SavedStateListener sl : savedListeners) {
			sl.stateChanged(false);
		}
	}

	public final boolean isSaved() {
		return isSaved || kernel.getConstruction() == null
				|| !kernel.getConstruction().isStarted();
	}

	public final boolean isAutoSaved() {
		return isAutoSaved;
	}

	public final void setAutoSaved() {
		isAutoSaved = true;
	}

	public final void setUnAutoSaved() {
		isAutoSaved = false;
	}

	/**
	 * Makes given view active
	 *
	 * @param evID
	 *            view id
	 */
	public abstract void setActiveView(int evID);

	/**
	 * Update backgrounds and repaint views.
	 */
	public void refreshViews() {
		getEuclidianView1().updateBackground();
		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateBackground();
		}
		kernel.notifyRepaint();
	}

	/**
	 * Switches the application to macro editing mode
	 *
	 * @param editMacro
	 *            Tool to be edited
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 */
	public void openMacro(Macro editMacro) {
		for (int i = 0; i < editMacro.getKernel().getMacroNumber(); i++) {
			if (editMacro.getKernel().getMacro(i) == editMacro) {
				break;
			}
			kernel.addMacro(editMacro.getKernel().getMacro(i));
		}
		String allXml = getXML();
		String header = allXml.substring(0, allXml.indexOf("<construction"));
		String footer = allXml.substring(allXml.indexOf("</construction>"),
				allXml.length());
		StringBuilder sb = new StringBuilder();
		editMacro.getXML(sb);
		String macroXml = sb.toString();
		String newXml = header
				+ macroXml.substring(macroXml.indexOf("<construction"),
						macroXml.indexOf("</construction>"))
				+ footer;
		this.macro = editMacro;
		setXML(newXml, true);
	}

	/**
	 * Open macro with given name in a new window.
	 * 
	 * @param macroName
	 *            macro name
	 */
	public void openMacro(String macroName) {
		Macro editMacro = getKernel().getMacro(macroName);
		Log.debug("[STORAGE] nr: " + getKernel().getMacroNumber()
				+ " macro for open is " + editMacro.getToolName());
		openMacro(editMacro);
	}

	/**
	 * Returns macro if in macro editing mode.
	 *
	 * @return macro being edited (in unchanged state)
	 */
	public Macro getMacro() {
		return macro;
	}

	/**
	 * @return XML for all macros; if there are none, XML header+footer are
	 *         returned
	 */
	public String getMacroXML() {
		ArrayList<Macro> macros = kernel.getAllMacros();
		return getXMLio().getFullMacroXML(macros);
	}

	/**
	 * @return XML for or macros and/or Exercise or empty string if there are
	 *         none
	 */
	public String getMacroXMLorEmpty() {
		if (!kernel.hasMacros() && kernel.getExercise().isEmpty()) {
			return "";
		}
		ArrayList<Macro> macros = kernel.getAllMacros();
		return getXMLio().getFullMacroXML(macros);
	}

	/**
	 * @param idx
	 *            secondary EV index, 1 for EV2
	 */
	public boolean hasEuclidianView2(int idx) {
		return (getGuiManager() != null)
				&& getGuiManager().hasEuclidianView2(idx);
	}

	/**
	 * Show localized message for an error.
	 * 
	 * @param e
	 *            error
	 */
	public final void showError(MyError e) {
		if (isWhiteboardActive()) {
			return;
		}
		String command = e.getcommandName();
		Log.debug("command: " + command);
		String message = e.getLocalizedMessage();
		if (command == null) {
			showErrorDialog(message);
			return;
		}
		getErrorHandler().showCommandError(command, message);
	}

	/**
	 * Unexpected exception: can't work out anything better, just show "Invalid
	 * Input"
	 * 
	 * @param e
	 *            exception
	 */
	public final void showGenericError(Exception e) {
		e.printStackTrace();
		showError(getLocalization().getErrorDefault("InvalidInput",
				"Please check your input"));
	}

	/**
	 * FKH
	 *
	 * @version 20040826
	 * @return full xml for GUI and construction
	 */
	public String getXML() {
		return getXMLio().getFullXML();
	}

	public abstract void showError(String string, String str);

	/**
	 * @param viewID
	 *            view id
	 * @return view with given ID
	 */
	public View getView(int viewID) {
		// check for PlotPanel ID family first
		if ((getGuiManager() != null)
				&& (getGuiManager().getPlotPanelView(viewID) != null)) {
			return getGuiManager().getPlotPanelView(viewID);
		}
		switch (viewID) {
		case VIEW_EUCLIDIAN:
			return getEuclidianView1();
		case VIEW_EUCLIDIAN3D:
			return getEuclidianView3D();
		case VIEW_ALGEBRA:
			return getAlgebraView();
		case VIEW_SPREADSHEET:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getSpreadsheetView();
		case VIEW_CAS:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getCasView();
		case VIEW_EUCLIDIAN2:
			return hasEuclidianView2(1) ? getEuclidianView2(1) : null;
		case VIEW_CONSTRUCTION_PROTOCOL:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getConstructionProtocolData();
		case VIEW_PROBABILITY_CALCULATOR:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getProbabilityCalculator();
		case VIEW_DATA_ANALYSIS:
			if (!isUsingFullGui()) {
				return null;
			} else if (getGuiManager() == null) {
				initGuiManager();
			}
			if (getGuiManager() == null) {
				return null;
			}
			return getGuiManager().getDataAnalysisView();
		}

		return null;
	}

	/**
	 * @param asPreference
	 *            true if we need this for prefs XML
	 * @return XML for user interface (EVs, spreadsheet, kernel settings)
	 */
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save gui tag settings
		sb.append(getGuiXML(asPreference));

		// save euclidianView settings
		getEuclidianView1().getXML(sb, asPreference);

		// save euclidian view 2 settings
		// TODO: the EV preferences should be serialized using
		// app.getSettings(), not the view
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			EuclidianView ev2 = getEuclidianView2(1);
			if (ev2 != null) {
				ev2.getXML(sb, asPreference);
			}
		}

		if (getGuiManager() != null) {
			// save spreadsheetView settings
			getGuiManager().getSpreadsheetViewXML(sb, asPreference);

			// save ProbabilityCalculator settings
			if (getGuiManager().hasProbabilityCalculator()) {
				getGuiManager().getProbabilityCalculatorXML(sb);
			}

			// save AlgebraView settings
			if (getGuiManager().hasAlgebraView()) {
				getGuiManager().getAlgebraViewXML(sb, asPreference);
			}

			// save Data Collection View settings
			if (getGuiManager().hasDataCollectionView()) {
				getGuiManager().getDataCollectionViewXML(sb, asPreference);
			}

		}

		if (asPreference) {
			getKeyboardXML(sb);
		}
		// coord style, decimal places settings etc
		kernel.getKernelXML(sb, asPreference);
		getScriptingXML(sb, asPreference);
		// save cas view seeting and cas session
		// if (casView != null) {
		// sb.append(((geogebra.cas.view.CASView) casView).getGUIXML());
		// sb.append(((geogebra.cas.view.CASView) casView).getSessionXML());
		// }

		return sb.toString();
	}

	private void getScriptingXML(StringBuilder sb, boolean asPreference) {
		sb.append("<scripting");
		if (getScriptingLanguage() != null) {
			sb.append(" language=\"");
			sb.append(getScriptingLanguage());
			sb.append("\"");
		}
		sb.append(" blocked=\"");
		sb.append(isBlockUpdateScripts());

		if (!asPreference) {
			sb.append("\" disabled=\"");
			sb.append(isScriptingDisabled());
		}

		sb.append("\"/>\n");
	}

	final public Settings getSettings() {
		return settings;
	}

	public final String getUniqueId() {
		return uniqueId;
	}

	public final void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public abstract void resetUniqueId();

	/**
	 * @param auxiliaryObjects
	 *            true to show Auxiliary objects
	 */
	public final void setShowAuxiliaryObjects(boolean auxiliaryObjects) {
		if (showAuxiliaryObjects == auxiliaryObjects) {
			return;
		}
		showAuxiliaryObjects = auxiliaryObjects;

		if (getGuiManager() != null) {
			getGuiManager().setShowAuxiliaryObjects(auxiliaryObjects);
			// updateMenubar();
		}
	}

	/**
	 * Returns labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 *
	 * @return labeling style for new objects
	 */
	public int getLabelingStyle() {
		return labelingStyle;
	}

	/**
	 * Sets labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 *
	 * @param labelingStyle
	 *            labeling style for new objects
	 */
	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
		labelingStyleSelected = true;
		getKernel().getConstruction().getConstructionDefaults()
				.resetLabelModeDefaultGeos();
	}

	/**
	 * @return labeling style for new objects for menu
	 */
	public int getLabelingStyleForMenu() {
		if (labelingStyleSelected) {
			return getLabelingStyle();
		}
		return -1;
	}

	/**
	 * set the labeling style not selected, i.e. at least one default geo has
	 * specific labeling style
	 */
	public void setLabelingStyleIsNotSelected() {
		labelingStyleSelected = false;
		if (getGuiManager() != null) {
			getGuiManager().updateMenubar();
		}
	}

	/**
	 * @return the scriptingDisabled
	 */
	public boolean isScriptingDisabled() {
		return scriptingDisabled;
	}

	/**
	 * @param sd
	 *            the scriptingDisabled to set
	 */
	public void setScriptingDisabled(boolean sd) {
		this.scriptingDisabled = sd;
	}

	/**
	 * @param size
	 *            preferred size
	 */
	public void setPreferredSize(GDimension size) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return timeout for tooltip disappearing (in seconds)
	 */
	public int getTooltipTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Sets tooltip timeout (in seconds)
	 *
	 * @param ttt
	 *            tooltip timeout
	 */
	public void setTooltipTimeout(int ttt) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param ttl
	 *            tooltip language
	 */
	public void setTooltipLanguage(String ttl) {
		// TODO Auto-generated method stub

	}

	public abstract DrawEquation getDrawEquation();

	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
	}

	/**
	 * Save all perspectives included in a document into an array with temporary
	 * perspectives.
	 *
	 * @param perspectives
	 *            array of perspetctives in the document
	 */
	public void setTmpPerspectives(ArrayList<Perspective> perspectives) {
		tmpPerspectives = perspectives;
	}

	/**
	 * @param idx
	 *            view index; 1 for EV2
	 * @return EV2
	 */
	public EuclidianView getEuclidianView2(int idx) {
		return null;
	}

	/**
	 * @return width of the whole application (central panel) This is needed for
	 *         Corner[6]
	 */
	public abstract double getWidth();

	/**
	 * @return height of the whole application (central panel) This is needed
	 *         for Corner[6]
	 */
	public abstract double getHeight();

	/**
	 *
	 * @param serif
	 *            serif
	 * @param style
	 *            font style
	 * @param size
	 *            font size
	 * @return font with given parameters
	 */
	public GFont getFontCommon(boolean serif, int style, int size) {
		return AwtFactory.getPrototype().newFont(serif ? "Serif" : "SansSerif",
				style, size);
	}

	/**
	 * In Desktop gives current font, in Web creates a new one
	 */
	public abstract GFont getPlainFontCommon();

	public boolean isExporting() {
		return exportType != ExportType.NONE;
	}

	public void setExporting(ExportType et, double scale) {
		exportType = et;
		exportScale = scale;
	}

	public ExportType getExportType() {
		return exportType;
	}

	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
	}

	public void setShowToolBarHelpNoUpdate(boolean toolbarHelp) {
		showToolBarHelp = toolbarHelp;
	}

	public boolean showToolBar() {
		return showToolBar;
	}

	public boolean showMenuBar() {
		return showMenuBar;
	}

	public void setShowMenuBar(boolean flag) {
		showMenuBar = flag;
	}

	public void setShowToolBar(boolean toolbar) {
		showToolBar = toolbar;
	}

	/**
	 * Update toolbar settings (no UI update)
	 * 
	 * @param toolbar
	 *            show toolbar?
	 * @param help
	 *            show toolbar help?
	 */
	public void setShowToolBar(boolean toolbar, boolean help) {
		showToolBar = toolbar;
		showToolBarHelp = help;
	}

	public int getToolbarPosition() {
		return toolbarPosition;
	}

	/**
	 * Update the toolbar position flag and optionally rebuilds the UI
	 * 
	 * @param position
	 *            new position
	 * @param update
	 *            whether to rebuild the UI
	 */
	public void setToolbarPosition(int position, boolean update) {
		// needs to be overridden
	}

	/**
	 * init the kernel (used for 3D)
	 */
	final public void initKernel() {
		kernel = companion.newKernel();
		// ensure that the selection manager is created
		getSelectionManager();
	}

	/**
	 * init the EuclidianView
	 */
	final public void initEuclidianViews() {

		euclidianController = newEuclidianController(kernel);
		euclidianView = newEuclidianView(showAxes, showGrid);
	}

	abstract protected EuclidianView newEuclidianView(boolean[] showAxes1,
			boolean showGrid1);

	public abstract EuclidianController newEuclidianController(Kernel kernel1);

	/**
	 * Returns undo manager
	 *
	 * @param cons
	 *            construction
	 * @return undo manager
	 */
	public abstract UndoManager getUndoManager(Construction cons);

	/**
	 * TODO refactor to remove this method Creates new animation manager
	 *
	 * @param kernel2
	 *            kernel
	 * @return animation manager
	 */
	public AnimationManager newAnimationManager(Kernel kernel2) {
		return new AnimationManager(kernel2);
	}

	/**
	 * TODO maybe we should create another factory for internal classes like
	 * this
	 *
	 * @return new graphics adapter for geo
	 */
	public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();

	/**
	 * Repaints the spreadsheet view
	 */
	public void repaintSpreadsheet() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return whether on the fly point creation is active
	 */
	public final boolean isOnTheFlyPointCreationActive() {
		return isOnTheFlyPointCreationActive;
	}

	/**
	 * @param isOnTheFlyPointCreationActive
	 *            Whether points can be created on the fly
	 */
	public final void setOnTheFlyPointCreationActive(
			boolean isOnTheFlyPointCreationActive) {
		this.isOnTheFlyPointCreationActive = isOnTheFlyPointCreationActive;
	}

	/**
	 * @return spreadsheet trace manager
	 */
	final public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null) {
			traceManager = new SpreadsheetTraceManager(this);
		}
		return traceManager;
	}

	/**
	 *
	 * @return true if there is a trace manager
	 */
	final public boolean hasTraceManager() {
		return traceManager != null;
	}

	/**
	 *
	 * @return true if at least one geo is traced
	 */
	final public boolean hasGeoTraced() {
		if (traceManager == null) {
			return false;
		}

		return traceManager.hasGeoTraced();
	}

	/**
	 * Switch current cursor to default cursor
	 */
	public void setDefaultCursor() {
		// TODO Auto-generated method stub

	}

	/**
	 * Switch current cursor to wait cursor
	 */
	public abstract void setWaitCursor();

	/**
	 * Update stylebars of all views
	 */
	public abstract void updateStyleBars();

	/**
	 * Update dynamic stylebars of all views
	 */
	public abstract void updateDynamicStyleBars();

	/**
	 * Changes current mode to move mode
	 */
	public void setMoveMode() {
		setMoveMode(ModeSetter.TOOLBAR);
	}

	/**
	 * Changes current mode to move mode
	 */
	public void setMoveMode(ModeSetter m) {
		setMode(EuclidianConstants.MODE_MOVE, m);
	}

	/**
	 * Changes current mode to mode of the toolbar's 1rst tool.
	 */
	public abstract void set1rstMode();

	/**
	 * @return spreadsheet table model
	 */
	public abstract SpreadsheetTableModel getSpreadsheetTableModel();

	/**
	 * Changes current mode (tool number)
	 *
	 * @param mode
	 *            new mode
	 */
	public void setMode(int mode, ModeSetter m) {
		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			currentSelectionListener = null;
		}
		if (getVideoManager() != null && mode != EuclidianConstants.MODE_MOVE) {
			getVideoManager().backgroundAll();
		}
		if (getGuiManager() != null) {
			setModeFromGuiManager(mode, m);
			this.updateDynamicStyleBars();

		} else if (euclidianView != null) {
			euclidianView.setMode(mode, m);
		}
	}

	protected void setModeFromGuiManager(int mode, ModeSetter m) {
		getGuiManager().setMode(mode, m);
	}

	/**
	 * Adds geo to Euclidian view (EV1)
	 *
	 * @param geo
	 *            geo
	 */
	public void addToEuclidianView(GeoElement geo) {
		geo.addView(App.VIEW_EUCLIDIAN);
		getEuclidianView1().add(geo);
	}

	/**
	 * Removes geo from Euclidian view (EV1)
	 *
	 * @param geo
	 *            geo
	 */
	public void removeFromEuclidianView(GeoElement geo) {
		geo.removeView(App.VIEW_EUCLIDIAN);
		getEuclidianView1().remove(geo);
	}

	/**
	 * Adds geo to 3D views
	 *
	 * @param geo
	 *            geo
	 */
	public void addToViews3D(GeoElement geo) {
		geo.addViews3D();
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().add(geo);
		}
	}

	/**
	 * Removes geo from 3D view
	 *
	 * @param geo
	 *            geo
	 */
	public void removeFromViews3D(GeoElement geo) {
		geo.removeViews3D();
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().remove(geo);
		}
	}

	public abstract void setXML(String string, boolean b);

	/**
	 * Returns API that can be used from external applications
	 *
	 * @return GeoGebra API
	 */
	public abstract GgbAPI getGgbApi();

	/**
	 * @return sound manager
	 */
	public abstract SoundManager getSoundManager();

	/**
	 * @return video manager
	 */
	public VideoManager getVideoManager() {
		return null;
	}

	/**
	 * @return kernel for this window
	 */
	public final Kernel getKernel() {
		return kernel;
	}

	/**
	 * @param e
	 *            event
	 * @return whether right mouse button was clicked or click + ctrl appeared
	 *         on Mac
	 */
	public boolean isRightClick(AbstractEvent e) {
		return e != null && e.isRightClick();
	}

	/**
	 * @param e
	 *            event
	 * @return whether Ctrl on Win/Linux or Meta on Mac was pressed
	 */
	public boolean isControlDown(AbstractEvent e) {
		return e != null && e.isControlDown();
	}

	/**
	 * @param e
	 *            event
	 * @return whether middle button was clicked once
	 */
	public boolean isMiddleClick(AbstractEvent e) {
		return e.isMiddleClick();
	}

	/**
	 * @return whether input bar is visible
	 */
	public abstract boolean showAlgebraInput();

	/**
	 * @return global key dispatcher. Can be null (eg Android, iOS)
	 */
	public abstract GlobalKeyDispatcher getGlobalKeyDispatcher();

	public abstract void callAppletJavaScript(String string, Object[] args);

	/**
	 * Inform current selection listener about newly (un)selected geo
	 *
	 * @param geo
	 *            (un)selected geo
	 * @param addToSelection
	 *            whether it should be added or removed from selection
	 */
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (currentSelectionListener != null) {
			currentSelectionListener.geoElementSelected(geo, addToSelection);
		}
	}

	/**
	 * Links properties view to this application
	 *
	 * @param propertiesView
	 *            properties view
	 */
	public void setPropertiesView(PropertiesView propertiesView) {
		this.propertiesView = propertiesView;
	}

	/**
	 * Sets a mode where clicking on an object will notify the given selection
	 * listener.
	 *
	 * @param sl
	 *            selection listener
	 */
	public void setSelectionListenerMode(GeoElementSelectionListener sl) {
		getSelectionManager().resetGeoToggled(); // prevent undo current tool
													// construction
		currentSelectionListener = sl;
		if (sl != null) {
			setMode(EuclidianConstants.MODE_SELECTION_LISTENER);
		} else {
			setMoveMode();
		}
	}

	/**
	 * Update stylebars and menubar (and possibly properties view) to match
	 * selection
	 *
	 * @param updatePropertiesView
	 *            whether to update properties view
	 */
	@Override
	public void updateSelection(boolean updatePropertiesView) {
		if (isIniting()) {
			return;
		}

		if (isUsingFullGui()) {
			if (getGuiManager() != null && showMenuBar) {
				getGuiManager().updateMenubarSelection();
			}

			// if showMenuBar is false, we can still update the style bars
			if (EuclidianConstants
					.isMoveOrSelectionMode(getActiveEuclidianView().getMode())
					|| getActiveEuclidianView()
							.getMode() == EuclidianConstants.MODE_TRANSLATEVIEW) {
				updateStyleBars();
			}

			if (has(Feature.DYNAMIC_STYLEBAR)) {
				// TODO update only dynamic stylebar
				updateStyleBars();
			}

			if (updatePropertiesView && propertiesView != null && showMenuBar) {
				propertiesView.updateSelection();
			}
		}
		ScreenReader.updateSelection(this);

	}

	/**
	 * @param type
	 *            what properties pannel should be showing (object, defults,
	 *            advanced, ...)
	 */
	public void setPropertiesViewPanel(OptionType type) {
		if (propertiesView != null) {
			propertiesView.setOptionPanel(type);
		}
	}

	/**
	 * @return whether this app is initializing
	 * @see #initing
	 */
	public boolean isIniting() {
		return initing;
	}

	/**
	 * @return whether shift, drag and zoom features are enabled
	 */
	public final boolean isShiftDragZoomEnabled() {
		return shiftDragZoomEnabled;
	}

	/**
	 * @param shiftDragZoomEnabled
	 *            whether shift, drag and zoom features are enabled
	 */
	public final void setShiftDragZoomEnabled(boolean shiftDragZoomEnabled) {
		this.shiftDragZoomEnabled = shiftDragZoomEnabled;
	}

	/**
	 * Updates menubar
	 */
	public abstract void updateMenubar();

	/**
	 * @return general font size (used for EV and GUI)
	 */
	public int getFontSize() {
		return appFontSize;
	}

	/**
	 * @return the font size for the Algebra View.
	 */
	public int getAlgebraFontSize() {
		return getFontSize() + 2;
	}

	/**
	 * Changes font size and possibly resets fonts
	 *
	 * @param points
	 *            font size
	 * @param update
	 *            whether fonts should be reset
	 * @see #resetFonts()
	 */
	public void setFontSize(int points, boolean update) {
		if (points == appFontSize) {
			return;
		}
		appFontSize = Util.getValidFontSize(points);
		// isSaved = false;
		if (!update) {
			return;
		}

		EuclidianView ev1 = getEuclidianView1();
		if (ev1 != null && ev1.hasStyleBar()) {
			ev1.getStyleBar().reinit();
		}

		if (hasEuclidianView2(1)) {
			EuclidianView ev2 = getEuclidianView2(1);
			if (ev2 != null && ev2.hasStyleBar()) {
				ev2.getStyleBar().reinit();
			}
		}

		if (isEuclidianView3Dinited() && getEuclidianView3D().hasStyleBar()) {
			getEuclidianView3D().getStyleBar().reinit();
		}

		resetFonts();

		updateUI();
	}

	/**
	 * Recursively update all components with current look and feel
	 */
	public abstract void updateUI();

	/**
	 * @return string representation of current locale, eg no_NO_NY
	 */

	/**
	 * Update font sizes of all components to match current GUI font size
	 */
	final public void resetFonts() {
		companion.resetFonts();
	}

	/**
	 * @return font size for GUI; if not specified, general font size is
	 *         returned
	 */
	public int getGUIFontSize() {
		return guiFontSize == -1 ? getFontSize() : guiFontSize;
	}

	/**
	 * @param size
	 *            GUI font size
	 */
	public void setGUIFontSize(int size) {
		guiFontSize = size;
		// updateFonts();
		// isSaved = false;

		resetFonts();

	}

	/**
	 * Returns font manager
	 * 
	 * @return font manager
	 */
	protected abstract FontManager getFontManager();

	/**
	 * Returns a font that can display testString in plain sans-serif font and
	 * current font size.
	 *
	 * @param testString
	 *            test string
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString) {
		return getFontCanDisplay(testString, false, GFont.PLAIN, getFontSize());
	}

	/**
	 * Returns a font that can display testString in given font style,
	 * sans-serif and current font size.
	 *
	 * @param testString
	 *            test string
	 * @param fontStyle
	 *            font style
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString, int fontStyle) {
		return getFontCanDisplay(testString, false, fontStyle, getFontSize());
	}

	/**
	 * Returns a font that can display testString and given font size.
	 *
	 * @param testString
	 *            test string
	 * @param serif
	 *            true=serif, false=sans-serif
	 * @param fontStyle
	 *            font style
	 * @param fontSize
	 *            font size
	 * @return font
	 */
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize) {
		return getFontManager().getFontCanDisplay(testString, serif, fontStyle,
				fontSize);
	}

	/**
	 * Returns gui settings in XML format
	 *
	 * @param asPreference
	 *            whether this is for preferences file
	 * @return gui settings in XML format
	 */
	public String getGuiXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();
		sb.append("<gui>\n");

		getWindowLayoutXML(sb, asPreference);

		sb.append("\t<font ");
		sb.append(" size=\"");
		sb.append(getFontSize());
		sb.append("\"/>\n");

		if (asPreference) {
			sb.append("\t<menuFont ");
			sb.append(" size=\"");
			sb.append(guiFontSize);
			sb.append("\"/>\n");

			sb.append("\t<tooltipSettings ");
			if (getLocalization().getTooltipLanguageString() != null) {
				sb.append(" language=\"");
				sb.append(getLocalization().getTooltipLanguageString());
				sb.append("\"");
			}
			sb.append(" timeout=\"");
			sb.append(getTooltipTimeout());
			sb.append("\"");

			sb.append("/>\n");
		}
		if (getGuiManager() != null) {
			getGuiManager().getExtraViewsXML(sb);
		}

		sb.append("</gui>\n");

		return sb.toString();
	}

	protected abstract int getWindowWidth();

	protected abstract int getWindowHeight();

	/**
	 * Appends layout settings in XML format to given builder
	 *
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether this is for preferences
	 */
	protected void getWindowLayoutXML(StringBuilder sb, boolean asPreference) {
		sb.append("\t<window width=\"");

		sb.append(getWindowWidth());

		sb.append("\" height=\"");

		sb.append(getWindowHeight());

		sb.append("\" />\n");

		getLayoutXML(sb, asPreference);

		// labeling style
		// default changed so we need to always save this now
		// if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
		sb.append("\t<labelingStyle ");
		sb.append(" val=\"");
		sb.append(getLabelingStyle());
		sb.append("\"/>\n");
	}

	protected abstract void getLayoutXML(StringBuilder sb,
			boolean asPreference);

	public abstract void reset();

	/**
	 * @return selection listener
	 */
	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	/**
	 * @param sl
	 *            selection listener
	 */
	public void setCurrentSelectionListener(GeoElementSelectionListener sl) {
		currentSelectionListener = sl;
	}

	/**
	 * @param flag
	 *            whether reset icon should be visible (in applets)
	 */
	public void setShowResetIcon(boolean flag) {
		if (flag != showResetIcon) {
			showResetIcon = flag;
			euclidianView.updateBackground();
		}
	}

	/**
	 * @return whether reset icon is visible
	 */
	final public boolean showResetIcon() {
		return showResetIcon && !runningInFrame;
	}

	/**
	 * @return whether undo manger can save undo info
	 */
	public boolean isUndoActive() {
		return kernel.isUndoActive();
	}

	/**
	 * (De)activate undo and redo, update toolbar
	 * 
	 * @param undoActive
	 *            whether undo should be active
	 */
	public void setUndoActive(boolean undoActive) {
		boolean flag = undoActive;
		// don't allow undo when data-param-EnableUndoRedo = false
		if (flag && !undoRedoEnabled) {
			flag = false;
		}

		if (kernel.isUndoActive() == flag) {
			return;
		}

		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (getGuiManager() != null) {
			getGuiManager().updateActions();
		}

		setSaved();
	}

	/**
	 * @return whether we are running in HTML5 applet
	 */
	public abstract boolean isHTML5Applet();

	/**
	 * @param useTransparentCursorWhenDragging
	 *            whether transparent cursor should be used while dragging
	 */
	public void setUseTransparentCursorWhenDragging(
			boolean useTransparentCursorWhenDragging) {
		this.useTransparentCursorWhenDragging = useTransparentCursorWhenDragging;
	}

	/**
	 * Update UI after redefinition
	 * 
	 * @param geo
	 *            redefined geo
	 */
	public void doAfterRedefine(GeoElementND geo) {
		if (getGuiManager() != null) {
			getGuiManager().doAfterRedefine(geo);
		}
	}

	/**
	 * Opens browser with given URL
	 *
	 * @param string
	 *            URL
	 */
	public abstract void showURLinBrowser(String string);

	/**
	 * Opens the upload to GGT dialog
	 */
	public abstract void uploadToGeoGebraTube();

	public boolean getUseFullGui() {
		return useFullGui;
	}

	public int getAppCanvasWidth() {
		return appCanvasWidth;
	}

	public int getAppCanvasHeight() {
		return appCanvasHeight;
	}

	/**
	 * @return where to show the inputBar (respective inputBox)
	 */
	public InputPosition getInputPosition() {
		return isUnbundled() ? InputPosition.algebraView : showInputTop;
	}

	/**
	 * Changes input position between bottom and top
	 *
	 * @param flag
	 *            whether input should be on top
	 * @param update
	 *            whether layout update is needed afterwards
	 */
	public void setInputPosition(InputPosition flag, boolean update) {
		if (flag == showInputTop) {
			return;
		}

		showInputTop = flag;

		if (update && !isIniting()) {
			updateApplicationLayout();
		}
	}

	/**
	 * @return whether innput help toggle button should be visible
	 */
	public boolean showInputHelpToggle() {
		return showInputHelpToggle;
	}

	/**
	 * Shows / hides input help toggle button
	 *
	 * @param flag
	 *            whether innput help toggle button should be visible
	 */
	public void setShowInputHelpToggle(boolean flag) {
		if (showInputHelpToggle == flag || getGuiManager() == null) {
			return;
		}

		showInputHelpToggle = flag;
		getGuiManager().updateAlgebraInput();
		updateMenubar();
	}

	/**
	 * Updates application layout
	 */
	public abstract void updateApplicationLayout();

	/**
	 * Returns name or help for given tool
	 *
	 * @param mode
	 *            mode number
	 * @param toolName
	 *            true for name, false for help
	 * @return tool name or help
	 */
	public String getToolNameOrHelp(int mode, boolean toolName) {
		// macro
		String ret;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			// MACRO
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro1 = kernel.getMacro(macroID);
				if (toolName) {
					// TOOL NAME
					ret = macro1.getToolOrCommandName();
				} else {
					// TOOL HELP
					ret = macro1.getToolHelp();
				}
			} catch (Exception e) {
				Log.debug(
						"Application.getModeText(): macro does not exist: ID = "
								+ macroID);
				// e.printStackTrace();
				return "";
			}
		} else {
			// STANDARD TOOL

			if (toolName) {
				// tool name
				String modeText = EuclidianConstants.getModeText(mode);
				ret = getLocalization().getMenu(modeText);
			} else {
				String modeText = EuclidianConstants.getModeTextSimple(mode);
				// tool help
				ret = getLocalization().getMenu(modeText + ".Help");
			}
		}

		return ret;
	}

	/**
	 * Returns name of given tool.
	 *
	 * @param mode
	 *            number
	 * @return name of given tool.
	 */
	public String getToolName(int mode) {
		return getToolNameOrHelp(mode, true);
	}

	/**
	 * Returns the tool help text for the given tool.
	 *
	 * @param mode
	 *            number
	 * @return the tool help text for the given tool.
	 */
	public String getToolHelp(int mode) {
		return getToolNameOrHelp(mode, false);
	}

	/**
	 * @return parser extension for functions
	 */
	public ParserFunctions getParserFunctions() {
		if (pf == null) {
			pf = new ParserFunctions();
		}
		pf.setInverseTrig(
				kernel.getLoadingMode() && kernel.getInverseTrigReturnsAngle());
		return pf;
	}

	/**
	 * Clears construction
	 *
	 * @return true if successful otherwise false (eg user clicks "Cancel")
	 */
	public abstract boolean clearConstruction();

	/**
	 * Clear construction and reset settings from preferences
	 */
	public abstract void fileNew();

	/**
	 * Remove references to dynamic bounds, reset selection rectangle
	 */
	protected void resetEVs() {
		if (kernel.getConstruction() != null) {
			kernel.getConstruction().setIgnoringNewTypes(true);
		}
		getEuclidianView1().resetXYMinMaxObjects();
		getEuclidianView1().setSelectionRectangle(null);
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			getEuclidianView2(1).resetXYMinMaxObjects();
			getEuclidianView2(1).setSelectionRectangle(null);
		}
		if (isEuclidianView3Dinited()) {
			getEuclidianView3D().resetXYMinMaxObjects();
			getEuclidianView3D().setSettingsToStandardView();
		}
		if (kernel.getConstruction() != null) {
			kernel.getConstruction().setIgnoringNewTypes(false);
		}
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 *
	 * @return random number in [0,1]
	 */
	public double getRandomNumber() {
		return random.nextDouble();
	}

	/**
	 * @param a
	 *            low value of distribution interval
	 * @param b
	 *            high value of distribution interval
	 * @return random number from Uniform Distribution[a,b]
	 */
	public double randomUniform(double a, double b) {
		return a + getRandomNumber() * (b - a);
	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 *
	 * @param low
	 *            least possible value of result
	 * @param high
	 *            highest possible value of result
	 *
	 * @return random integer between a and b inclusive (or NaN for
	 *         getRandomIntegerBetween(5.5, 5.5))
	 *
	 */
	public int getRandomIntegerBetween(double low, double high) {
		// make sure 4.000000001 is not rounded up to 5
		double a = DoubleUtil.checkInteger(low);
		double b = DoubleUtil.checkInteger(high);

		// Math.floor/ceil to make sure
		// RandomBetween[3.2, 4.7] is between 3.2 and 4.7
		int min = (int) Math.ceil(Math.min(a, b));
		int max = (int) Math.floor(Math.max(a, b));

		// eg RandomBetween[5.499999, 5.500001]
		// eg RandomBetween[5.5, 5.5]
		if (min > max) {
			int tmp = max;
			max = min;
			min = tmp;
		}

		return random.nextInt(max - min + 1) + min;

	}

	/**
	 * allows use of seeds to generate the same sequence for a ggb file
	 *
	 * @param seed
	 *            new seed
	 */
	public void setRandomSeed(int seed) {
		random = new Random(seed);
	}

	public abstract boolean loadXML(String xml) throws Exception;

	/**
	 * copy bitmap of EV to clipboard
	 */
	public abstract void copyGraphicsViewToClipboard();

	/**
	 * copy base64 of current .ggb file to clipboard
	 */
	public void copyBase64ToClipboard() {
		copyTextToSystemClipboard(getGgbApi().getBase64());
	}

	/**
	 * copy full HTML5 export for current .ggb file to clipboard
	 */
	public void copyFullHTML5ExportToClipboard() {
		copyTextToSystemClipboard(HTML5Export.getFullString(this));
	}

	/**
	 * 
	 * @param url
	 *            url
	 * @return url converted to a data URI if possible. If not, returns the URL
	 *         unaltered
	 */
	protected String convertImageToDataURIIfPossible(String url) {
		return url;
	}

	/**
	 * Resets active EV to standard
	 */
	public final void setStandardView() {
		getActiveEuclidianView().setStandardView(true);
	}

	public abstract void exitAll();

	/**
	 * Full version eg X.Y.Zd-prerelease
	 * 
	 * @return version string
	 */
	public String getVersionString() {

		if (version != null) {
			return version.getVersionString(prerelease, canary);
		}

		// fallback in case version not set properly
		return GeoGebraConstants.VERSION_STRING + "?";
	}

	public abstract NormalizerMinimal getNormalizer();

	public final void zoom(double px, double py, double zoomFactor) {
		getActiveEuclidianView().zoom(px, py, zoomFactor, 15, true);
	}

	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 *
	 * @param axesratio
	 *            axes scale ratio
	 */
	public final void zoomAxesRatio(double axesratio) {
		getActiveEuclidianView().zoomAxesRatio(axesratio, 1, true);
	}

	/**
	 * Zooms and pans active EV to show all objects
	 *
	 * @param keepRatio
	 *            true to keep ratio of axes
	 */
	public final void setViewShowAllObjects(boolean keepRatio) {
		getActiveEuclidianView().setViewShowAllObjects(true, keepRatio);
	}

	/**
	 * @return whether right click features are enabled
	 */
	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	/**
	 * Enables or disables right clicking in this application. This is useful
	 * for applets.
	 *
	 * @param flag
	 *            whether right click features should be enabled
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}

	/**
	 * @return whether right click features are enabled for Algebra View
	 */
	final public boolean isRightClickEnabledForAV() {
		return rightClickEnabledForAV;
	}

	/**
	 * Enables or disables right clicking for Algebra View. Used e.g. in Exam
	 * Simple Calc app
	 *
	 * @param flag
	 *            whether right click features should be enabled
	 */
	public void setRightClickEnabledForAV(boolean flag) {
		rightClickEnabledForAV = flag;
	}

	/**
	 * @return whether context menu is enabled
	 */
	public final boolean letShowPopupMenu() {
		return rightClickEnabled;
	}

	/**
	 * @return whether properties dialog is enabled
	 */
	public boolean letShowPropertiesDialog() {
		return rightClickEnabled;
	}

	/**
	 * @return preferences XML
	 */
	public String getPreferencesXML() {
		return getXMLio().getPreferencesXML();
	}

	/**
	 * @param geo1
	 *            geo
	 * @param string
	 *            parameter (for input box scripts)
	 */
	public abstract void runScripts(GeoElement geo1, String string);

	/**
	 * @param type
	 *            JS or GGBScript
	 * @param scriptText0
	 *            possibly localized text
	 * @param translate
	 *            whether to convert from localized
	 * @return sript object
	 */
	public Script createScript(ScriptType type, String scriptText0,
			boolean translate) {
		String scriptText = scriptText0;
		if (type == ScriptType.GGBSCRIPT && translate) {
			scriptText = GgbScript.localizedScript2Script(this, scriptText);
		}
		return type.newScript(this, scriptText);
	}

	/**
	 * Attach GGBScript runner to event dispatcher
	 */
	public void startGeoScriptRunner() {
		if (geoScriptRunner == null) {
			geoScriptRunner = new GeoScriptRunner(this);
			getEventDispatcher().addEventListener(geoScriptRunner);
		}
	}

	/**
	 * Compares 2, 3 or 4 objects by using the Relation Tool.
	 *
	 * @param ra
	 *            first object
	 * @param rb
	 *            second object
	 * @param rc
	 *            third object (optional, can be null)
	 * @param rd
	 *            forth object (optional, can be null)
	 *
	 * @author Zoltan Kovacs
	 */
	public void showRelation(final GeoElement ra, final GeoElement rb,
			final GeoElement rc, final GeoElement rd) {
		Relation.showRelation(this, ra, rb, rc, rd);
	}

	public GeoElement getGeoForCopyStyle() {
		return geoForCopyStyle;
	}

	public void setGeoForCopyStyle(GeoElement geo) {
		geoForCopyStyle = geo;
	}

	public abstract CASFactory getCASFactory();

	public abstract Factory getFactory();

	public void dispatchEvent(Event evt) {
		getEventDispatcher().dispatchEvent(evt);
	}

	/**
	 * @return XML input / output utility
	 */
	public MyXMLio getXMLio() {
		if (myXMLio == null) {
			myXMLio = createXMLio(kernel.getConstruction());
		}
		return myXMLio;
	}

	public abstract MyXMLio createXMLio(Construction cons);

	public boolean hasEventDispatcher() {
		return eventDispatcher != null;
	}

	/**
	 * This should not be used, just overriden in AppW
	 */
	public void scheduleUpdateConstruction() {
		kernel.getConstruction().updateConstructionLaTeX();
		kernel.notifyRepaint();
	}

	/**
	 * Show or hide the input bar.
	 * 
	 * @param flag
	 *            whwther to show
	 * @param update
	 *            whether to update UI
	 */
	public void setShowAlgebraInput(boolean flag, boolean update) {
		showAlgebraInput = flag;

		if (update) {
			updateApplicationLayout();
			updateMenubar();
		}
	}

	public void setNeedsSpreadsheetTableModel() {
		needsSpreadsheetTableModel = true;
	}

	public boolean needsSpreadsheetTableModel() {
		return needsSpreadsheetTableModel;
	}

	public final int getAppletWidth() {
		return appletWidth;
	}

	public void setAppletWidth(int width) {
		this.appletWidth = width;
	}

	public int getAppletHeight() {
		return appletHeight;
	}

	public void setAppletHeight(int height) {
		this.appletHeight = height;
	}

	public void startCollectingRepaints() {
		getEuclidianView1().getEuclidianController()
				.startCollectingMinorRepaints();
	}

	public void stopCollectingRepaints() {
		getEuclidianView1().getEuclidianController()
				.stopCollectingMinorRepaints();
	}

	public abstract Localization getLocalization();

	/**
	 * @return selction manager
	 */
	public SelectionManager getSelectionManager() {
		if (selection == null) {
			selection = new SelectionManager(getKernel(), this);
		}
		return selection;
	}

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 *
	 * @param mode
	 *            : tool ID
	 */
	public String getToolTooltipHTML(int mode) {
		StringBuilder sbTooltip = new StringBuilder();
		if (isUnbundled()) {
			sbTooltip.append("<html><p>");
			sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
			sbTooltip.append("</p>");
			if (getWidth() >= 400) {
				sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
			}
		} else {
			sbTooltip.append("<html><b>");
			sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
			sbTooltip.append("</b><br>");
			sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
		}

		sbTooltip.append("</html>");
		return sbTooltip.toString();
	}

	/**
	 * Make sure we start a new penstroke.
	 */
	public void resetPen() {
		getEuclidianView1().getEuclidianController().resetPen();

		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).getEuclidianController().resetPen();
		}
	}

	/**
	 * @param id
	 *            view ID
	 * @return whether navigation bar in given view needs update
	 */
	public boolean getShowCPNavNeedsUpdate(int id) {
		if (showConstProtNavigationNeedsUpdate == null) {
			return false;
		}

		Boolean update = showConstProtNavigationNeedsUpdate.get(id);
		if (update == null) {
			return false;
		}
		return update;
	}

	private boolean getShowCPNavNeedsUpdate() {
		if (showConstProtNavigationNeedsUpdate == null) {
			return false;
		}

		for (boolean update : showConstProtNavigationNeedsUpdate.values()) {
			if (update) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return whether navigation bar is shown in at least one view
	 */
	public boolean showConsProtNavigation() {
		if (showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			return true;
		}
		if (showConsProtNavigation == null) {
			return false;
		}

		for (boolean show : showConsProtNavigation.values()) {
			if (show) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add space separated list of view IDs that are showing navigation bar
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void getConsProtNavigationIds(StringBuilder sb) {
		if (showConsProtNavigation == null) {
			if (showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
				sb.append(App.VIEW_CONSTRUCTION_PROTOCOL);
			}
			return;
		}
		boolean alreadyOne = false;
		for (Entry<Integer, Boolean> entry : showConsProtNavigation
				.entrySet()) {
			int id = entry.getKey();
			if (entry.getValue()) {
				if (alreadyOne) {
					sb.append(" ");
				} else {
					alreadyOne = true;
				}
				sb.append(id);
			}
		}
	}

	/**
	 * @param id
	 *            view ID
	 * @return whether navigation bar is shown in view with that ID
	 */
	public boolean showConsProtNavigation(int id) {
		if (id == App.VIEW_CONSTRUCTION_PROTOCOL) {
			return true;
		}
		if (showConsProtNavigation == null) {
			return false;
		}

		Boolean show = showConsProtNavigation.get(id);
		if (show == null) {
			return false;
		}

		return show;
	}

	/**
	 * @param show
	 *            whether navigation bar should be visible
	 * @param playButton
	 *            whether play button should be visible
	 * @param playDelay
	 *            delay between phases (in seconds)
	 * @param showProtButton
	 *            whether button to show construction protocol should be visible
	 */
	public void setShowConstructionProtocolNavigation(boolean show, int id,
			boolean playButton, double playDelay, boolean showProtButton) {

		ConstructionProtocolSettings cpSettings = getSettings()
				.getConstructionProtocol();
		cpSettings.setShowPlayButton(playButton);
		cpSettings.setPlayDelay(playDelay);
		cpSettings.setShowConsProtButton(showProtButton);

		if (getGuiManager() != null) {
			getGuiManager().applyCPsettings(cpSettings);
		}

		setShowConstructionProtocolNavigation(show, id);

		if (getGuiManager() != null) {

			if (show) {
				getGuiManager().setShowConstructionProtocolNavigation(show, id,
						playButton, playDelay, showProtButton);
			}
		}

	}

	/**
	 * Hide all navigation bars or set the flags if UI not loaded.
	 */
	public void setHideConstructionProtocolNavigation() {
		if (!showConsProtNavigation() && (!getShowCPNavNeedsUpdate())) {
			return;
		}

		if (showConsProtNavigation == null) {
			return;
		}

		if (getGuiManager() != null) {
			for (int id : showConsProtNavigation.keySet()) {
				showConsProtNavigation.put(id, false);
				getGuiManager().setShowConstructionProtocolNavigation(false,
						id);
				setShowConstProtNavigationNeedsUpdate(id, false);
			}
		} else {
			for (int id : showConsProtNavigation.keySet()) {
				setShowConstProtNavigationNeedsUpdate(id, true);
			}
		}
	}

	private void setShowConstProtNavigationNeedsUpdate(int id, boolean flag) {
		if (showConstProtNavigationNeedsUpdate == null) {
			showConstProtNavigationNeedsUpdate = new HashMap<>();
		}
		Boolean update = showConstProtNavigationNeedsUpdate.get(id);
		if (update == null || update != flag) {
			showConstProtNavigationNeedsUpdate.put(id, flag);
		}
	}

	/**
	 * Displays the construction protocol navigation
	 *
	 * @param flag
	 *            true to show navigation bar
	 */
	public void setShowConstructionProtocolNavigation(boolean flag) {
		dispatchEvent(
				new Event(EventType.SHOW_NAVIGATION_BAR, null, flag + ""));
		if (!flag) {
			setHideConstructionProtocolNavigation();
		} else {
			if (!showConsProtNavigation()) {
				// show navigation bar in active view
				setShowConstructionProtocolNavigation(true,
						getActiveEuclidianView().getViewID());
				return;

			} else if (!getShowCPNavNeedsUpdate()) {
				return;
			}

			if (getGuiManager() != null) {
				for (int id : showConsProtNavigation.keySet()) {
					showConsProtNavigation.put(id, true);
					getGuiManager().setShowConstructionProtocolNavigation(true,
							id);
					setShowConstProtNavigationNeedsUpdate(id, false);
				}
			} else {
				for (int id : showConsProtNavigation.keySet()) {
					setShowConstProtNavigationNeedsUpdate(id, true);
				}
			}
		}
	}

	/**
	 * Displays the construction protocol navigation
	 *
	 * @param flag
	 *            true to show navigation bar
	 */
	public void setShowConstructionProtocolNavigation(boolean flag, int id) {

		if (showConsProtNavigation == null) {
			showConsProtNavigation = new HashMap<>();
		} else {
			if ((flag == showConsProtNavigation(id))
					&& (!getShowCPNavNeedsUpdate(id))) {
				return;
			}
		}
		showConsProtNavigation.put(id, flag);
		dispatchEvent(new Event(EventType.SHOW_NAVIGATION_BAR, null,
				"[" + flag + "," + id + "]"));
		if (getGuiManager() != null) {
			getGuiManager().setShowConstructionProtocolNavigation(flag, id);
			setShowConstProtNavigationNeedsUpdate(id, false);
		} else {
			setShowConstProtNavigationNeedsUpdate(id, true);
		}
	}

	/**
	 * Switch navigation bar buttons to pause.
	 */
	public void setNavBarButtonPause() {
		if (getGuiManager() != null) {
			getGuiManager().setNavBarButtonPause();
		}
	}

	/**
	 * Switch navigation bar buttons to play.
	 */
	public void setNavBarButtonPlay() {
		if (getGuiManager() != null) {
			getGuiManager().setNavBarButtonPlay();
		}
	}

	/**
	 * Toggle navigation bar on given view.
	 * 
	 * @param id
	 *            view ID
	 */
	public void toggleShowConstructionProtocolNavigation(int id) {

		setShowConstructionProtocolNavigation(!showConsProtNavigation(id), id);

		setUnsaved();
		// updateCenterPanel(true);

		if (getGuiManager() != null) {
			getGuiManager()
					.updateCheckBoxesForShowConstructinProtocolNavigation(id);
		}

	}

	/**
	 * @param mode
	 *            app mode ID
	 */
	public GImageIcon wrapGetModeIcon(int mode) {
		// TODO: debug message commented out from Trunk version, probably loops
		// Log.debug("App.wrapGetModeIcon must be overriden");
		return null;
	}

	/**
	 *
	 * useful for benchmarking ie only useful for elapsed time
	 *
	 * accuracy will depend on the platform / browser uses
	 * System.nanoTime()/1000000 in Java performance.now() in JavaScript
	 *
	 * Won't return sub-millisecond accuracy
	 *
	 * Chrome: doesn't work for sub-millisecond yet
	 * https://code.google.com/p/chromium/issues/detail?id=158234
	 *
	 * @return millisecond time
	 */
	public abstract double getMillisecondTime();

	/**
	 * Update undo/redo and menu for selection.
	 */
	public void updateActions() {
		if (isUsingFullGui() && getGuiManager() != null) {
			getGuiManager().updateActions();
		}
	}

	public void doRepaintViews() {
		// TODO Auto-generated method stub
	}

	/**
	 * @return SignInOperation eventFlow
	 */
	public LogInOperation getLoginOperation() {
		return loginOperation;
	}

	/**
	 * This method is to be overridden in subclasses In Web, this can run in
	 * asyncronous mode
	 *
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @return AlgoKimberlingWeightsInterface
	 */
	public abstract AlgoKimberlingWeightsInterface getAlgoKimberlingWeights();

	/**
	 * Needed for running part of AlgoKimberling async
	 * <p/>
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @param kw
	 *            kimberling weights
	 * @return
	 */
	public abstract double kimberlingWeight(AlgoKimberlingWeightsParams kw);

	/**
	 * This method is to be overridden in subclasses In Web, this can run in
	 * asyncronous mode
	 *
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @return AlgoCubicSwitchInterface
	 */
	public abstract AlgoCubicSwitchInterface getAlgoCubicSwitch();

	/**
	 * Needed for running part of AlgoKimberling async
	 *
	 * ** MUST STAY AS ABSTRACT OTHERWISE WEB PROJECT DOESN'T GET SPLIT UP **
	 *
	 * @param cw
	 *            cubic weights
	 * @return cubic equation
	 */
	public abstract String cubicSwitch(AlgoCubicSwitchParams cw);

	public abstract CommandDispatcher getCommandDispatcher(Kernel k);

	/**
	 * Should lose focus on Web applets, implement only where appropriate
	 */
	public void loseFocus() {
		Log.debug(
				"Should lose focus on Web applets, ipmelment (override) only where appropriate");
	}

	/**
	 * Whether the app is running just to create a screenshot, some
	 * recomputations can be avoided in such case
	 *
	 * @return false by defaul, overridden in AppW
	 */
	public boolean isScreenshotGenerator() {
		return false;
	}

	public final boolean isErrorDialogsActive() {
		return isErrorDialogsActive;
	}

	public final void setErrorDialogsActive(boolean isErrorDialogsActive) {
		this.isErrorDialogsActive = isErrorDialogsActive;
	}

	/**
	 * Recompute coord systems in EV and spreadsheet Only needed in web,
	 * 
	 */
	public void updateViewSizes() {
		// overwritten in AppW
	}

	public void persistWidthAndHeight() {
		// overwritten in AppW
	}

	protected AppCompanion newAppCompanion() {
		return new AppCompanion(this);
	}

	public AppCompanion getCompanion() {
		return companion;
	}

	public SensorLogger getSensorLogger() {
		return null;
	}

	/**
	 * Add file open listener.
	 * 
	 * @param openListener
	 *            listener
	 */
	public void registerOpenFileListener(OpenFileListener openListener) {
		if (openFileListener == null) {
			this.openFileListener = new ArrayList<>();
		}
		this.openFileListener.add(openListener);
	}

	/**
	 * Remove file open listener.
	 * 
	 * @param openListener
	 *            listener
	 */
	public void unregisterOpenFileListener(OpenFileListener openListener) {
		if (openFileListener == null) {
			return;
		}
		this.openFileListener.remove(openListener);
	}

	protected void onOpenFile() {
		if (this.openFileListener != null) {
			for (OpenFileListener listener : openFileListener) {
				listener.onOpenFile();
			}

		}
	}

	/**
	 * @return whether more than one of EV1 and EV2 is showing
	 */
	public boolean isShowingMultipleEVs() {
		if (getGuiManager() == null
				|| getGuiManager().getEuclidianViewCount() < 2) {
			return false;
		}
		for (int i = 1; i < getGuiManager().getEuclidianViewCount(); i++) {
			if (getGuiManager().hasEuclidianView2(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAllowPopups() {
		return allowPopUps;
	}

	public void setAllowPopups(boolean b) {
		allowPopUps = b;
	}

	/**
	 * @param query
	 *            search query
	 */
	public void openSearch(String query) {
		// TODO Auto-generated method stub
	}

	/**
	 * Adds a macro from XML
	 *
	 * @param xml
	 *            macro code (including &lt;macro> wrapper)
	 * @return True if successful
	 */
	public boolean addMacroXML(String xml) {
		boolean ok = true;
		try {
			getXMLio().processXMLString(
					"<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT
							+ "\">" + xml + "</geogebra>",
					false, true);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
			ok = false;
		} catch (Exception e) {
			e.printStackTrace();
			ok = false;
			localizeAndShowError("LoadFileFailed");
		}
		return ok;
	}

	/**
	 * Tells if given View is enabled. 3D be disabled by using command line
	 * option "--show3D=disable". CAS be disabled by using command line option
	 * "--showCAS=disable".
	 *
	 * @return whether the 3D view is enabled
	 */
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_EUCLIDIAN3D) {
			return _3DViewEnabled && this.is3D();
		}
		if (viewID == App.VIEW_CAS) {
			return CASViewEnabled;
		}
		return true;
	}

	public void ensureTimerRunning() {
		// only for Web

	}

	public abstract void showCustomizeToolbarGUI();

	public abstract boolean isSelectionRectangleAllowed();

	protected boolean isNativeMobileAppWithNewUI() {
		return false;
	}

	/**
	 * Check if featue is supported; depends on prerelease/ canary flags and
	 * platform / app name.
	 * 
	 * @param f
	 *            feature
	 * @return whether it's supported
	 */
	public final boolean has(Feature f) {
		boolean whiteboard = isWhiteboardActive();
		boolean relaunch = true;
		// boolean keyboard = true;
		switch (f) {

		// **********************************************************************
		// MOBILE START
		// note: please use prefix MOB
		// *********************************************************
		// **********************************************************************

		// MOB-270
		case ACRA:
			return prerelease;

		case ANALYTICS:
			return prerelease;

		// MOB-788
		case MOBILE_USE_FBO_FOR_3D_IMAGE_EXPORT:
			return false;

		// MOB-1319
		case MOB_NOTIFICATION_BAR_TRIGGERS_EXAM_ALERT_IOS_11:
			return false;

		// IGR-605
		case MOB_TABBED_SETTINGS_PANEL:
			return true;

		// AND-722
		case MOB_MATERIAL_INPUT:
			return true;

		// AND-679
		case MOB_AND_QUEUE_ON_GL_THREAD:
			return true;

		// IGR-748
		case MOB_EXPORT_IMAGE:
			return true;

		// IGR-601
		case MOB_HELP_FEEDBACK:
			return true;

		// MOB-1513
		case MOB_PACK_JOIN_POINTS:
			return true;

		// MOB-1514
		case MOB_PACK_CONIC:
			return true;

		// MOB-1515
		case MOB_PACK_POINTS:
			return true;

		// MOB-1537
		case MOB_PREVIEW_WHEN_EDITING:
			return false;

		// MOB-1516
		case MOB_PACK_ALL_CURVES:
			return true;

		// AND-1061 and IGR-815
		case MOB_EXAM_MODE_EXIT_DIALOG_NEW:
			return true;

		// AND-1071 and IGR-819
		case MOB_EXAM_MODE_SCREENSHOT_SAVED_NOTIFICATION:
			return prerelease;

		// AND-887 and IGR-732
		case MOB_PROPERTY_SORT_BY:
			return false;

		// MOB-1668
		case MOB_LAYER_FOR_PACKING:
			return true;

		// IGR-898
		case MOB_REACT_SEARCH_VIEW:
			return prerelease;

		// **********************************************************************
		// MOBILE END
		// *********************************************************
		// **********************************************************************

		// **********************************************************************
		// MOW START
		// note: please use prefix MOW
		// *********************************************************
		// **********************************************************************

		case WHITEBOARD_APP:
			return prerelease;

		// MOW-29
		case MOW_TOOLBAR:
			return prerelease && whiteboard; // prerelease;

		case MOW_CONTEXT_MENU:
			return relaunch && isUnbundledOrWhiteboard();

		/** MOW-55 */
		case MOW_BOUNDING_BOXES:
			return prerelease && whiteboard;

		/** MOW-320 */
		case MOW_PIN_IMAGE:
			return prerelease && whiteboard;
			
		case MOW_PEN_IS_LOCUS:
			return prerelease;

		case MOW_PEN_EVENTS:
			return false;

		case MOW_PEN_SMOOTHING:
			return prerelease;

		case MOW_AXES_STYLE_SUBMENU:
			return prerelease && whiteboard;

		case MOW_IMPROVE_CONTEXT_MENU:
			return prerelease && whiteboard;

		case MOW_CLEAR_VIEW_STYLEBAR:
			return relaunch && isUnbundledOrWhiteboard();

		case MOW_COLORPOPUP_IMPROVEMENTS:
			return prerelease;

		case MOW_DIRECT_FORMULA_CONVERSION:
			return false;

		/** MOW-368 */
		case MOW_IMAGE_DIALOG_UNBUNDLED:
			return prerelease && whiteboard;

		/** MOW-261 */
		case MOW_COLOR_FILLING_LINE:
			return prerelease && whiteboard;

		/** MOW-269 */
		case MOW_MULTI_PAGE:
			return prerelease && whiteboard;

		/** MOW-336 */
		case MOW_DRAG_AND_DROP_PAGES:
			return prerelease && whiteboard;

		/** MOW-336 */
		case MOW_DRAG_AND_DROP_ANIMATION:
			return prerelease && whiteboard;

		/** MOW-360, MOW-381, MOW-382 */
		case MOW_CROP_IMAGE:
			return prerelease && whiteboard;

		/** MOW-379, MOW-380 */
		case MOW_IMAGE_BOUNDING_BOX:
			return prerelease && whiteboard;

		/** MOW-285 */
		case MOW_BOUNDING_BOX_FOR_PEN_TOOL:
			return prerelease && whiteboard;

		/** MOW-345 */
		case MOW_MOVING_CANVAS:
			return prerelease && whiteboard;

		/** MOW-349 */
		case MOW_AUDIO_TOOL:
			return prerelease && whiteboard;

		/** MOW-299 */
		case MOW_VIDEO_TOOL:
			return canary && whiteboard;

		/** MOW-278 */
		case MOW_HIGHLIGHTER_TOOL:
			return canary && whiteboard;

		/** MOW-459 */
		case MOW_DOUBLE_CANVAS:
			return canary && whiteboard;

		// **********************************************************************
		// MOW END
		// *********************************************************
		// **********************************************************************

		// **********************************************************************
		// KEYBOARD START
		// *******************************************************
		// **********************************************************************

		// GGB-1349

		// GGB-1252
		case KEYBOARD_BEHAVIOUR:
			return !whiteboard; // we may need keyboard for videos in
								// whiteboard; also overriding keyboard kills
								// Paste (MOW-442)

		/**
		 * GGB-1398 + GGB-1529
		 */
		case SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME:
			return true;

		/** GGB-2336 */
		case KEYBOARD_ATTACHED_TO_TABLET:
			return false;
		// **********************************************************************
		// KEYBOARD END
		// *********************************************************
		// **********************************************************************

		// leave as prerelease
		case TUBE_BETA:
			return prerelease;

		// leave as prerelease
		case ALL_LANGUAGES:
			return prerelease;

		/** GGB-2366 */
		case TIKZ_AXES:
			return true;

		/** GGB-650 */
		case GGB_WEB_ASSEMBLY:
			return true;

		case SOLVE_QUARTIC:
			return prerelease;

		case AUTOMATIC_DERIVATIVES:
			return canary;

		case EXERCISES:
			return prerelease;

		// when moved to stable, move ImplicitSurface[] from TABLE_ENGLISH
		// in Command.Java
		case IMPLICIT_SURFACES:
			return prerelease;

		case CONTOUR_PLOT_COMMAND:
			return prerelease;

		case LOCALSTORAGE_FILES:
			return prerelease || Versions.WEB_FOR_DESKTOP.equals(getVersion());

		case POLYGON_TRIANGULATION:
			return false;

		case TOOL_EDITOR:
			return prerelease;

		// GGB-776
		case ABSOLUTE_TEXTS:
			return false;

		// TRAC-4845
		case LOG_AXES:
			return prerelease;

		case HIT_PARAMETRIC_SURFACE:
			return false;

		case PARAMETRIC_SURFACE_IS_REGION:
			return prerelease;

		case CONVEX_HULL_3D:
			return canary;

		case COMMAND_HOLES:
			return prerelease;

		// GGB-2318
		case SPLIT_INTEGRAL_IF:
			return true;

		case HANDWRITING:
			return false;
		case WEB_CLASSIC_FLOATING_MENU:
			return canary;
		// GGB-92
		case AV_DEFINITION_AND_VALUE:
			return !isDesktop();

		case DATA_COLLECTION:
			return version != null && version != Versions.WEB_FOR_DESKTOP;

		// in web (not tablet apps yet)
		// File -> Enter Exam Mode
		case EXAM_TABLET:
			return prerelease;

		case SAVE_SETTINGS_TO_FILE:
			// not enabled for linux
			return isWindows() || isMacOS() || prerelease;

		// GGB-334, TRAC-3401
		case ADJUST_WIDGETS:
			return false;

		// GGB-944
		case EXPORT_ANIMATED_PDF:
			return prerelease;

		case AUTOSCROLLING_SPREADSHEET:
			return prerelease;

		case ERASER:
			return prerelease;

		case ROUNDED_POLYGON:
			return prerelease;

		case DYNAMIC_STYLEBAR:
			return relaunch && isUnbundledOrWhiteboard();

		case AV_ITEM_DESIGN:
			return relaunch || isNativeMobileAppWithNewUI();

		case EXPORT_SCAD_IN_MENU:
			return prerelease;

		case EXPORT_COLLADA_IN_MENU:
			return false;

		case EXPORT_OBJ_IN_MENU:
			return false;

		case INPUT_BAR_ADD_SLIDER:
			return relaunch && isHTML5Applet();

		case DEFAULT_OBJECT_STYLES:
			return relaunch || isNativeMobileAppWithNewUI();

		case OBJECT_DEFAULTS_AND_COLOR:
			return relaunch && isUnbundledOrWhiteboard();

		case SHOW_STEPS:
			return prerelease;

		case LABEL_SETTING_ON_STYLEBAR:
			return relaunch && !whiteboard;

		case SURFACE_2D:
			return prerelease;

		case DYNAMIC_STYLEBAR_SELECTION_TOOL:
			return relaunch;

		case CENTER_STANDARD_VIEW:
			return relaunch;

		/** GGB-1966 */
		case FUNCTIONS_DYNAMIC_STYLEBAR_POSITION:
			return relaunch;

		/** GGB-1982 */
		// TODO if there is no need for this feature flag more, remove "appl"
		// parameter from MyCJButton.MyCJButton(App appl)
		case OPENING_DYNAMIC_STYLEBAR_ON_FIXED_GEOS:
			return relaunch;

		case FLOATING_SETTINGS:
			return isUnbundledOrWhiteboard() && relaunch;

		/** GGB-2005 */
		case TOOLTIP_DESIGN:
			return isUnbundledOrWhiteboard() && relaunch;

		case INITIAL_PORTRAIT:
			return isUnbundled() && relaunch;

		/** GGB-1986 */
		case DIALOG_DESIGN:
			return isUnbundledOrWhiteboard() && relaunch;

		/** GGB-2015 */
		case GEO_AV_DESCRIPTION:
			return relaunch;

		/** MOW-390 GGB */
		case WHOLE_PAGE_DRAG:
			return true;

		/** GGB-2053 */
		case TAB_ON_GUI:
			return true;

		/** MOB-1293 */
		case SELECT_TOOL_NEW_BEHAVIOUR:
			return prerelease;

		/** GGB-2118 */
		case PREVIEW_POINTS:
			// not ggb5
			return getVersion() != Versions.DESKTOP;

		/** GGB-2183 */
		case AUTO_ADD_DEGREE:
			return getKernel().degreesMode();

		/** GGB-2222 */
		case CHANGE_INVERSE_TRIG_TO_DEGREES:
			return getKernel().degreesMode();

		/** GGB-2170 */
		case SLIDER_STYLE_OPTIONS:
			return true;

		/** MOB-1310 */
		case SHOW_HIDE_LABEL_OBJECT_DELETE_MULTIPLE:
			return prerelease;

		/** GGB-2203 */
		case HELP_AND_SHORTCUTS:
			return true;

		/** GGB-2203 */
		case HELP_AND_SHORTCUTS_IMPROVEMENTS:
			return true;

		/** GGB-2347 */
		case READ_DROPDOWNS:
			return prerelease;

		/** GGB-2204 */
		case TAB_ON_MENU:
			return true;

		/** GGB-2258 */
		case VOICEOVER_CURSOR:
			return true;

		/** GGB-1717 */
		case IMAGE_EXPORT:
			return true;

		/** GGB-2346 */
		case CURRENCY_UNIT:
			return true;

		default:
			Log.debug("missing case in Feature: " + f);
			return false;

		}
	}

	public boolean isUnbundled() {
		return false;
	}

	public boolean isUnbundledGraphing() {
		return false;
	}

	public boolean isUnbundledGeometry() {
		return false;
	}

	public boolean isWhiteboardActive() {
		return false;
	}

	public boolean isUnbundledOrWhiteboard() {
		return isUnbundled() || isWhiteboardActive();
	}

	public boolean canResize() {
		return !isApplet();
	}

	/**
	 *
	 * @return true if we want to use shaders
	 */
	public boolean useShaders() {
		return false;
	}

	public final int getTubeId() {
		return tubeID;
	}

	public final void setTubeId(int uniqueId) {
		this.tubeID = uniqueId;
	}

	public boolean hasFocus() {
		return true;
	}

	final public boolean hasEuclidianViewForPlane() {
		return companion.hasEuclidianViewForPlane();
	}

	final public boolean hasEuclidianViewForPlaneVisible() {
		return companion.hasEuclidianViewForPlaneVisible();
	}

	final public EuclidianView getViewForPlaneVisible() {
		return companion.getViewForPlaneVisible();
	}

	/**
	 * add to views for plane (if any)
	 *
	 * @param geo
	 *            geo
	 */
	final public void addToViewsForPlane(GeoElement geo) {
		companion.addToViewsForPlane(geo);
	}

	public boolean isModeValid(int mode) {
		return mode >= 0 && !"".equals(getToolName(mode));
	}

	public void updateKeyboard() {
		// TODO Auto-generated method stub
	}

	/**
	 * handle space key hitted
	 *
	 * @return true if key is consumed
	 */
	public boolean handleSpaceKey() {
		ArrayList<GeoElement> selGeos = selection.getSelectedGeos();
		if (selGeos.size() == 1) {
			GeoElement geo = selGeos.get(0);
			if (geo.isGeoBoolean()) {
				GeoBoolean geoBool = (GeoBoolean) selGeos.get(0);
				geoBool.setValue(!geoBool.getBoolean());
				geoBool.updateRepaint();
			} else if (geo.isGeoInputBox()) {
				getActiveEuclidianView()
						.focusAndShowTextField((GeoInputBox) geo);
			} else if (geo.isGeoList() && ((GeoList) geo).drawAsComboBox()) {
				Drawable d = (Drawable) getActiveEuclidianView()
						.getDrawableFor(geo);
				((DrawDropDownList) d).toggleOptions();

			} else if (geo.isGeoNumeric()) {

				// <Space> -> toggle slider animation off/on
				GeoNumeric num = (GeoNumeric) geo;
				if (num.isAnimatable()) {
					num.setAnimating(!num.isAnimating());

					storeUndoInfo();
					// update play/pause icon at bottom left
					getActiveEuclidianView().repaint();

					if (num.isAnimating()) {
						num.getKernel().getAnimatonManager().startAnimation();
					}

				}

			} else {

				geo.runClickScripts(null);
			}

			return true;
		}

		return false;

	}

	public void setAltText() {
		// ignored in desktop

	}

	/**
	 * remove from views for plane (if any)
	 *
	 * @param geo
	 *            geo
	 */
	final public void removeFromViewsForPlane(GeoElement geo) {
		companion.removeFromViewsForPlane(geo);
	}

	public int getFontSizeWeb() {
		return Math.max(getFontSize(), 12);
	}

	/**
	 * @return if sliders are displayed in the AV
	 */
	public boolean showAutoCreatedSlidersInEV() {
		return true;
	}

	public ExamEnvironment getExam() {
		return exam;
	}

	public boolean isExam() {
		return exam != null;
	}

	public boolean isExamStarted() {
		return isExam() && getExam().isStarted();
	}

	public void setExam(ExamEnvironment exam) {
		this.exam = exam;
	}

	public void setNewExam() {
		setExam(new ExamEnvironment(this));
	}

	public void startExam() {
		getExam().setStart((new Date()).getTime());
	}

	/**
	 * @param lang
	 *            locale description
	 */
	public void setLanguage(String lang) {
		// overridden in subtypes
	}

	public void isShowingLogInDialog() {
		// TODO Auto-generated method stub

	}

	/**
	 * Run a callback on each view.
	 * 
	 * @param c
	 *            view callback
	 */
	public void forEachView(ViewCallback c) {
		if (getGuiManager().showView(App.VIEW_ALGEBRA)) {
			c.run(App.VIEW_ALGEBRA, "AlgebraWindow");
		}
		if (getGuiManager().showView(App.VIEW_CAS)) {
			c.run(App.VIEW_CAS, "CAS");
		}
		if (getGuiManager().showView(App.VIEW_SPREADSHEET)) {
			c.run(App.VIEW_SPREADSHEET, "Spreadsheet");
		}
		if (getGuiManager().showView(App.VIEW_EUCLIDIAN)) {
			c.run(App.VIEW_EUCLIDIAN, "DrawingPad");
		}
		if (getGuiManager().showView(App.VIEW_EUCLIDIAN2)) {
			c.run(App.VIEW_EUCLIDIAN2, "DrawingPad2");
		}
		if (getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			c.run(App.VIEW_CONSTRUCTION_PROTOCOL, "ConstructionProtocol");
		}
		if (getGuiManager().showView(App.VIEW_DATA_ANALYSIS)) {
			c.run(App.VIEW_DATA_ANALYSIS, "DataAnalysis");
		}

	}

	public String getInput3DType() {
		return Input3DConstants.PREFS_NONE;
	}

	public abstract void closePopups();

	public void closePopups(int x, int y) {
		closePopups();
		getActiveEuclidianView().closeDropDowns(x, y);
	}

	public String getExportTitle() {
		String title = getKernel().getConstruction().getTitle();
		return "".equals(title) ? "geogebra-export" : title;
	}

	public double getExportScale() {
		return this.exportScale;
	}

	/**
	 * Creates a new Timer.
	 *
	 * @param delay
	 *            Milliseconds to run timer after start()1.
	 * @return GTimer descendant instance.
	 */
	public abstract GTimer newTimer(GTimerListener listener, int delay);

	/**
	 * @param geo
	 *            slider to be read by screen reader
	 */
	public void readLater(GeoNumeric geo) {
		// implemented in AppW
	}

	/**
	 * possible positions for the inputBar (respective inputBox)
	 */
	public enum InputPosition {
		/**
		 * inputBox in the AV
		 */
		algebraView,
		/**
		 * inputBar at the top
		 */
		top,
		/**
		 * inputBar at the bottom
		 */
		bottom
	}

	public enum ExportType {
		NONE, PDF_TEXTASSHAPES, PDF_EMBEDFONTS, PDF_HTML5, EPS, EMF,

		PNG, PNG_BRAILLE, SVG, PRINTING, ANIMATED_GIF, WEBP, WEBM;

		/**
		 * @return minus sign for axes description
		 */
		public char getAxisMinusSign() {
			switch (this) {
			case PDF_HTML5:
			case PDF_EMBEDFONTS:
			case PNG_BRAILLE:
				return '-';

			default:
				return Unicode.N_DASH;
			}
		}
	}

	/**
	 * state to know if we'll need to store undo info
	 */
	private enum CoordSystemStateForUndo {
		/** tells that the mouse has been pressed */
		MAY_SET_COORD_SYSTEM,
		/** tells that the coord system has changed */
		SET_COORD_SYSTEM_OCCURED,
		/** no particular state */
		NONE
	}

	public interface ViewCallback {
		public void run(int viewID, String viewName);
	}

	final public boolean loadXML(byte[] zipFile) {
		return loadXML(new ByteArrayZipFile(zipFile));
	}

	/**
	 * Opens a file.
	 * 
	 * @param zipFile
	 *            ggb file
	 * @return success
	 */
	final public boolean loadXML(ZipFile zipFile) {
		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);

			getXMLio().readZipFromString(zipFile);

			kernel.initUndoInfo();
			setSaved();
			resetCurrentFile();
			// command list may have changed due to macros
			updateCommandDictionary();

			hideDockBarPopup();
			return true;
		} catch (Exception err) {
			resetCurrentFile();
			err.printStackTrace();
			return false;
		}
	}

	public void resetCurrentFile() {
		//
	}

	protected void hideDockBarPopup() {
		// only used in desktop
	}

	public void schedulePreview(Runnable scheduledPreview) {
		// this is basic implementation with no scheduled delay
		scheduledPreview.run();
	}

	public void cancelPreview() {
		// not needed in basic implementation
	}

	/**
	 * Get url to eg play an MP3.
	 * 
	 * @param id
	 *            matrial ID
	 * @return download URL
	 */
	public String getURLforID(String id) {
		String url;
		if (has(Feature.TUBE_BETA)) {
			url = GeoGebraConstants.GEOGEBRA_WEBSITE_BETA;
		} else {
			url = GeoGebraConstants.GEOGEBRA_WEBSITE;
		}

		// something like
		// http://www.geogebra.org/files/material-1264825.mp3
		url = url + "material/download/format/file/id/" + id;
		return url;
	}

	public ErrorHandler getErrorHandler() {
		return getDefaultErrorHandler();
	}

	public ErrorHandler getDefaultErrorHandler() {
		return ErrorHelper.silent();
	}

	/**
	 * 
	 * @return true if running in native "desktop" Java
	 */
	public boolean isDesktop() {
		return false;
	}

	/**
	 * 
	 * @return true if running on native Android (not WebView)
	 */
	protected boolean isAndroid() {
		return false;
	}

	/**
	 * Change rounding; setting may contain "s" for scientific digits or "r" to
	 * prefer rationals.
	 * 
	 * @param rounding
	 *            rounding description
	 */
	public void setRounding(String rounding) {
		if (rounding.length() > 0) {
			StringBuilder roundingNum = new StringBuilder("0");
			for (int i = 0; i < rounding.length(); i++) {
				if (rounding.charAt(i) <= '9' && rounding.charAt(i) >= '0') {
					roundingNum.append(rounding.charAt(i));
				}
			}
			int roundInt = Integer.parseInt(roundingNum.toString());
			if (rounding.contains("s")) {
				getKernel().setPrintFigures(roundInt);
			} else {
				getKernel().setPrintDecimals(roundInt);
			}
			if (rounding.contains("r")) {
				GeoElement defNumber = getKernel().getConstruction()
						.getConstructionDefaults()
						.getDefaultGeo(ConstructionDefaults.DEFAULT_NUMBER);
				if (defNumber != null) {
					((GeoNumeric) defNumber).setSymbolicMode(true, false);
				}
			}
		}
	}

	public void examWelcome() {
		// TODO Auto-generated method stub

	}

	public GBufferedImage getActiveEuclidianViewExportImage(double maxX,
			double maxY) {
		return getEuclidianViewExportImage(getActiveEuclidianView(), maxX,
				maxY);
	}

	protected static GBufferedImage getEuclidianViewExportImage(
			EuclidianView ev, double maxX, double maxY) {

		double scale = Math.min(maxX / ev.getSelectedWidthInPixels(),
				maxY / ev.getSelectedHeightInPixels());

		return ev.getExportImage(scale);
	}

	public void batchUpdateStart() {
		// used in android
	}

	public void batchUpdateEnd() {
		// used in android
	}

	/**
	 * Adjust widgets on screen.
	 * 
	 * @param reset
	 *            whether to reset the stored offsets
	 */
	public void adjustScreen(boolean reset) {
		if (!kernel.getApplication().has(Feature.ADJUST_WIDGETS)) {
			return;
		}
		if (adjustScreen == null) {
			adjustScreen = new AdjustScreen(getEuclidianView1());
		}
		if (!reset) {
			adjustScreen.restartButtons();
		}
		adjustScreen.apply(reset);
		if (this.hasEuclidianView2(1)) {
			if (adjustScreen2 == null) {
				adjustScreen2 = new AdjustScreen(getEuclidianView2(1));
			}
			if (!reset) {
				adjustScreen2.restartButtons();
			}
			adjustScreen2.apply(reset);
		}
	}

	/**
	 * Adjusts Algebra and Euclidian View next to or bellow each other
	 * (Portrait) according to app size.
	 * 
	 * @param force
	 *            TODO
	 * 
	 * @return if screen became portrait or not.
	 */
	public boolean adjustViews(boolean reset, boolean force) {
		if (adjustViews == null) {
			adjustViews = new AdjustViews(this);
		}

		adjustViews.apply(force);
		adjustScreen(reset);

		return adjustViews.isPortait();
	}

	public Versions getVersion() {
		return version;
	}

	/**
	 * Description of a labeling style.
	 * 
	 * @param app
	 *            app
	 * @param id
	 *            labeling style (GeoElement.LABEL_*)
	 * @return localized labeling style
	 */
	final public static String getLabelStyleName(App app, int id) {
		switch (id) {
		case (-1):
			return app.getLocalization().getMenu("Hidden");
		case (GeoElement.LABEL_NAME):
			return app.getLocalization().getMenu("Name");
		case (GeoElement.LABEL_NAME_VALUE):
			return app.getLocalization().getMenu("NameAndValue");
		case (GeoElement.LABEL_VALUE):
			return app.getLocalization().getMenu("Value");
		case (GeoElement.LABEL_CAPTION):
			return app.getLocalization().getMenu("Caption");
		default:
			return "";
		}
	}

	/**
	 * @return copy/paste utility
	 */
	public CopyPaste getCopyPaste() {
		// return 2D version in AppD, AppW, AppWSimple
		if (copyPaste == null) {
			copyPaste = new CopyPaste3D();
		}

		return copyPaste;
	}

	/**
	 * Update view settings with size from XML
	 * 
	 * @param evSet
	 *            view settings
	 */
	public void ensureEvSizeSet(EuclidianSettings evSet) {
		// only for applets
	}

	/**
	 * 
	 * @return 9999 (or 200 in web)
	 */
	public int getMaxSpreadsheetRowsVisible() {
		return Kernel.MAX_SPREADSHEET_ROWS_DESKTOP;
	}

	/**
	 * 
	 * @return 9999 (or 200 in web)
	 */
	public int getMaxSpreadsheetColumnsVisible() {
		return Kernel.MAX_SPREADSHEET_COLUMNS_DESKTOP;
	}

	/**
	 * @return whether Singular web service was initialized properly
	 */
	public boolean singularWSisAvailable() {
		return singularWS != null && singularWS.isAvailable();
	}

	/**
	 * @param s
	 *            CAS command
	 * @return command translated to Singular
	 */
	public String singularWSgetTranslatedCASCommand(String s) {
		if (singularWS == null) {
			return null;
		}
		return singularWS.getTranslatedCASCommand(s);
	}

	/**
	 * @param s
	 *            singular command
	 * @return singular answer
	 * @throws Throwable
	 *             when command invalid or problem with Singular occurs
	 */
	public String singularWSdirectCommand(String s) throws Throwable {
		if (singularWS == null) {
			return null;
		}
		return singularWS.directCommand(s);
	}

	abstract public void invokeLater(Runnable runnable);

	/**
	 * 
	 * @param callback
	 *            GeoGebraToPstricks object
	 */
	public void newGeoGebraToPstricks(
			AsyncOperation<GeoGebraExport> callback) {
		// overridden in AppD, AppW
	}

	/**
	 * 
	 * @param callback
	 *            GeoGebraToAsymptote object
	 */
	public void newGeoGebraToAsymptote(
			AsyncOperation<GeoGebraExport> callback) {
		// overridden in AppD, AppW
	}

	/**
	 * 
	 * @param callback
	 *            GeoGebraToPgf object
	 */
	public void newGeoGebraToPgf(AsyncOperation<GeoGebraExport> callback) {
		// overridden in AppD, AppW
	}

	/**
	 * @param text
	 *            text to be copied
	 */
	public void copyTextToSystemClipboard(String text) {
		// overridden in AppD, AppW
	}

	/**
	 * last commands selected from help (used in Android & iOS native)
	 * 
	 * @param commandName
	 *            last command
	 */
	public void addToLastCommandsSelectedFromHelp(String commandName) {
		if (mLastCommandsSelectedFromHelp == null) {
			mLastCommandsSelectedFromHelp = new ArrayList<>();
		}
		// remove if already in it
		mLastCommandsSelectedFromHelp.remove(commandName);
		mLastCommandsSelectedFromHelp.add(commandName);
	}

	/**
	 * @return last commands selected from help (used in Android & iOS native)
	 */
	public ArrayList<String> getLastCommandsSelectedFromHelp() {
		return mLastCommandsSelectedFromHelp;
	}

	protected EuclidianController getEuclidianController() {
		return euclidianController;
	}

	final public boolean useTransparentCursorWhenDragging() {
		return useTransparentCursorWhenDragging;
	}

	public AlgoKimberlingWeightsInterface getKimberlingw() {
		return kimberlingw;
	}

	public AlgoKimberlingWeightsInterface setKimberlingw(
			AlgoKimberlingWeightsInterface kimberlingw) {
		this.kimberlingw = kimberlingw;
		return kimberlingw;
	}

	public AlgoCubicSwitchInterface getCubicw() {
		return cubicw;
	}

	public AlgoCubicSwitchInterface setCubicw(AlgoCubicSwitchInterface cubicw) {
		this.cubicw = cubicw;
		return cubicw;
	}

	/**
	 * SMART and Android WebView apps use special native Giac Other web apps use
	 * giac.js GGB6 WebView uses giac.js for now, see GGB-895 Everything else
	 * uses native Giac
	 * 
	 * @return true if using native Giac, false if using giac.js
	 */
	public boolean nativeCAS() {
		return true;
	}

	/**
	 * @return next construction element ID
	 */
	public long getNextCeIDcounter() {
		return ceIDcounter++;
	}

	/**
	 * @return next prover variable ID
	 */
	public int getNextVariableID() {
		return nextVariableID++;
	}

	/**
	 * @return tool categorization for this app
	 */
	public ToolCategorization createToolCategorization() {
		ToolCategorization.AppType type;
		boolean isPhoneApp = false;
		switch (getVersion()) {
		case ANDROID_NATIVE_GRAPHING:
		case IOS_NATIVE:
			type = ToolCategorization.AppType.GRAPHING_CALCULATOR;
			isPhoneApp = true;
			break;
		case ANDROID_GEOMETRY:
		case IOS_GEOMETRY:
			type = ToolCategorization.AppType.GEOMETRY_CALC;
			isPhoneApp = true;
			break;
		case ANDROID_NATIVE_3D:
			type = ToolCategorization.AppType.GRAPHER_3D;
			isPhoneApp = true;
			break;
		case WEB_GRAPHING:
		case WEB_GRAPHING_OFFLINE:
			type = ToolCategorization.AppType.GRAPHING_CALCULATOR;
			break;
		case WEB_GEOMETRY:
		case WEB_GEOMETRY_OFFLINE:
			type = ToolCategorization.AppType.GEOMETRY_CALC;
			break;
		case WEB_3D_GRAPHING:
			type = ToolCategorization.AppType.GRAPHER_3D;
			break;
		default:
			type = ToolCategorization.AppType.GRAPHING_CALCULATOR;
			break;
		}

		// Needed temporary, until the toolset levels are not implemented on iOS
		// too
		getSettings().getToolbarSettings().setType(type);
		return new ToolCategorization(this, type,
				getSettings().getToolbarSettings().getToolsetLevel(),
				isPhoneApp);
	}

	/**
	 * set export will be done on next 3D frame
	 * 
	 * @param format
	 *            export format
	 */
	public void setExport3D(Format format) {
		companion.setExport3D(format);
	}

	public boolean isPortrait() {
		return getHeight() > getWidth();
	}

	public AppConfig getConfig() {
		return new AppConfigDefault();
	}

	/**
	 * 
	 * @return the AccessibilityManager.
	 */
	public AccessibilityManagerInterface getAccessibilityManager() {
		return new AccessibilityManagerNoGui();
	}

	/**
	 * GGB-2171
	 * 
	 * @param b
	 *            set whether buttons have shadows
	 */
	public void setButtonShadows(boolean b) {
		this.buttonShadows = b;
	}

	/**
	 * GGB-2171
	 * 
	 * @param percent
	 *            set how rounded buttons are
	 */
	public void setButtonRounding(double percent) {
		if (!MyDouble.isFinite(percent)) {
			this.buttonRounding = 0.2;
		} else if (percent < 0) {
			this.buttonRounding = 0;
		} else if (percent > 0.9) {
			this.buttonRounding = 0.9;
		} else {
			this.buttonRounding = percent;
		}
	}

	/**
	 * GGB-2171
	 * 
	 * @return how rounded buttons are
	 */
	public double getButtonRouding() {
		return buttonRounding;
	}

	/**
	 * GGB-2171
	 * 
	 * @return whether buttons have shadows
	 */
	public boolean getButtonShadows() {
		return buttonShadows;
	}

	/**
	 * check is view is 3D WITHOUT creating 3D View
	 * 
	 * @param view
	 *            view
	 * @return true if it's 3D
	 */
	public boolean isEuclidianView3D(EuclidianViewInterfaceCommon view) {
		return false;
	}

	/**
	 * Hides burger menu on web
	 */
	public void hideMenu() {
		// overwritten in web
	}

	/**
	 * Lazy load special points manager
	 * 
	 * @return special points manager
	 */
	public SpecialPointsManager getSpecialPointsManager() {
		if (this.specialPointsManager == null) {
			specialPointsManager = new SpecialPointsManager(kernel);
			specialPointsManager.registerSpecialPointsListener(kernel);
			specialPointsManager.registerSpecialPointsListener(euclidianController);
		}
		return specialPointsManager;
	}

	public boolean hasSpecialPointsManager() {
		return specialPointsManager != null;
	}

	/**
	 * enable/disable CAS and re-init command table
	 * 
	 * @param enable
	 *            whether to enable CAS
	 */
	public void enableCAS(boolean enable) {
		getSettings().getCasSettings().setEnabled(enable);
		getKernel().getAlgebraProcessor().reinitCommands();
	}

	/**
	 * enable/disable CAS and re-init command table
	 *
	 * @param cas
	 *            if CAS is enabled
	 * @param commands3d
	 *            if 3D commands are enabled
	 */
	public void enableCAS3D(boolean cas, boolean commands3d) {
		areCommands3DEnabled = commands3d;
		enableCAS(cas);
	}

	public boolean areCommands3DEnabled() {
		return areCommands3DEnabled;
	}

	/**
	 * @param fallback
	 *            value in case temp perspective is not defined
	 * @return perspective called "tmp" or given fallback
	 */
	public Perspective getTmpPerspective(Perspective fallback) {
		if (tmpPerspectives == null) {
			return fallback;
		}
		for (Perspective perspective : tmpPerspectives) {
			if (perspective.getId().equals("tmp")) {
				return perspective;
			}
		}
		return fallback;
	}

	/**
	 * 
	 * @param ext
	 *            extension
	 * @param content
	 *            contents of file
	 * 
	 */
	public void exportStringToFile(String ext, String content) {
		// needs to be implemented in subclasses
	}

	/**
	 * handle image as appropriate
	 * web: show in lightbox
	 * mobile: share intent (TODO)
	 * desktop: copy to clipboard
	 * 
	 * @param base64image base64 encoded PNG/SVG/PDF
	 */
	public void handleImageExport(String base64image) {
		// overridden in AppW, AppD, etc
		
	}

	/**
	 * @param action
	 *            command to execute
	 * @param state
	 *            file content
	 * @param args
	 *            arguments
	 * 
	 */
	public void executeAction(EventType action, AppState state, String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param slideID
	 *            slide name
	 */
	public void setActiveSlide(String slideID) {
		// TODO Auto-generated method stub
	}

	/**
	 *
	 * @return available calculator types for exam mode
	 */
	public ExamEnvironment.CalculatorType[] getAvailableExamCalculatorTypes() {
		return new ExamEnvironment.CalculatorType[] { ExamEnvironment.CalculatorType.GRAPHING,
				ExamEnvironment.CalculatorType.SYMBOLIC };
	}

	/**
	 * @param imgFileName
	 *            file name
	 * @param imgBase64
	 *            base64 of image content
	 * @param imageOld
	 *            old image
	 * @param autoCorners
	 *            whether to create corners
	 */
	public GeoImage createImageFromString(final String imgFileName,
			String imgBase64, GeoImage imageOld, boolean autoCorners) {
		return null;
	}

	public String md5Encrypt(String s) {
		return getMD5EncrypterStatic().encrypt(s);
	}

	/**
	 * @return MD5 encrypter that can be used in GWT
	 */
	public static synchronized MD5EncrypterGWTImpl getMD5EncrypterStatic() {
		if (md5Encrypter == null) {
			md5Encrypter = new MD5EncrypterGWTImpl();
		}
		return md5Encrypter;
	}

}
