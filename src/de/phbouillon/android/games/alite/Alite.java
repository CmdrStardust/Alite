package de.phbouillon.android.games.alite;

/* Alite - Discover the Universe on your Favorite Android Device
 * Copyright (C) 2015 Philipp Bouillon
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful and
 * fun, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * http://http://www.gnu.org/licenses/gpl-3.0.txt.
 */

import java.io.IOException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.io.FileUtils;
import de.phbouillon.android.games.alite.io.ObbExpansionsManager;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.Rating;
import de.phbouillon.android.games.alite.model.generator.GalaxyGenerator;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.missions.ConstrictorMission;
import de.phbouillon.android.games.alite.model.missions.CougarMission;
import de.phbouillon.android.games.alite.model.missions.EndMission;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.missions.SupernovaMission;
import de.phbouillon.android.games.alite.model.missions.ThargoidDocumentsMission;
import de.phbouillon.android.games.alite.model.missions.ThargoidStationMission;
import de.phbouillon.android.games.alite.screens.NavigationBar;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.BuyScreen;
import de.phbouillon.android.games.alite.screens.canvas.DiskScreen;
import de.phbouillon.android.games.alite.screens.canvas.EquipmentScreen;
import de.phbouillon.android.games.alite.screens.canvas.GalaxyScreen;
import de.phbouillon.android.games.alite.screens.canvas.HackerScreen;
import de.phbouillon.android.games.alite.screens.canvas.InventoryScreen;
import de.phbouillon.android.games.alite.screens.canvas.LibraryScreen;
import de.phbouillon.android.games.alite.screens.canvas.LoadingScreen;
import de.phbouillon.android.games.alite.screens.canvas.LocalScreen;
import de.phbouillon.android.games.alite.screens.canvas.PlanetScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.OptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutorialSelectionScreen;
import de.phbouillon.android.games.alite.screens.opengl.DefaultCoordinateTransformer;
import de.phbouillon.android.games.alite.screens.opengl.HyperspaceScreen;
import de.phbouillon.android.games.alite.screens.opengl.TextureManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.LaserManager;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteFont;

public class Alite extends AndroidGame {
	public static final String VERSION_STRING = AliteConfig.VERSION_STRING + " " + (AliteConfig.HAS_EXTENSION_APK ? "OBB" : "SFI"); 
	public static final String LOG_IS_INITIALIZED = "logIsInitialized";
	
	private Player player;
	private GalaxyGenerator generator;
	private long startTime;
	private long elapsedTime;
	private NavigationBar navigationBar;
	private int hackerId;
	private static AliteScreen definingScreen;
	private final FileUtils fileUtils;
	private LaserManager laserManager;
	private AliteFont font;
	private static Alite alite;
	private boolean saving = false;
	
	public Alite() {
		super(1920, 1080);		
		fileUtils = new FileUtils();
		alite = this;
	}

	@Override
	public Screen getStartScreen() {
		return new LoadingScreen(this);
	}
		
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);		
		AndroidUtil.setImmersion(getCurrentView());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent == null || !intent.getBooleanExtra(LOG_IS_INITIALIZED, false)) {
			AliteLog.initialize(getFileIO());			
		}
		AliteLog.d("Alite.onCreate", "onCreate begin");
		final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
	            AliteLog.e("Uncaught Exception (Alite)", "Message: " + (paramThrowable == null ? "<null>" : paramThrowable.getMessage()), paramThrowable);
				if (oldHandler != null) {
					oldHandler.uncaughtException(paramThread, paramThrowable);
				} else {
					System.exit(2);
				}
			}
		});
		AliteLog.d("Alite.onCreate", "initializing");
		initialize();
		startTime = System.nanoTime();
		registerMissions();
		switch (Settings.lockScreen) {
			case 0: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); break;
			case 1: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
			case 2: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break;
		}				
		AliteLog.d("Alite.onCreate", "onCreate end");
	}
	
	public void initialize() {
		if (generator == null) {
			generator = new GalaxyGenerator();
			generator.buildGalaxy(1);
		}
		if (player == null) {
			player = new Player(this);
		}
	}
	
	private void registerMissions() {
		MissionManager.getInstance().clear();
		MissionManager.getInstance().register(new ConstrictorMission(this));
		MissionManager.getInstance().register(new ThargoidDocumentsMission(this));
		MissionManager.getInstance().register(new SupernovaMission(this));
		MissionManager.getInstance().register(new CougarMission(this));
		MissionManager.getInstance().register(new ThargoidStationMission(this));
		MissionManager.getInstance().register(new EndMission(this));
	}
	
	public void activateHacker() {
		if (hackerId != 0) {
			navigationBar.setVisible(hackerId, true);
		}
	}
	
	public boolean isHackerActive() {
		if (hackerId == 0) {
			return false;
		}
		return navigationBar.isVisible(hackerId);
	}
	
	public void setIntergalActive(boolean active) {
	}
	
	public void setGameTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
		this.startTime = System.nanoTime();
	}
	
	public long getGameTime() {
		return elapsedTime + (System.nanoTime() - startTime);
	}
	
	public FileUtils getFileUtils() {
		return fileUtils;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public PlayerCobra getCobra() {
		return player.getCobra();
	}
	
	public GalaxyGenerator getGenerator() {
		return generator;
	}

	public StatusScreen getStatusScreen() {
		return new StatusScreen(this);
	}

	public BuyScreen getBuyScreen() {
		return new BuyScreen(this);
	}
	
	public InventoryScreen getInventoryScreen() {
		return new InventoryScreen(this);
	}

	public EquipmentScreen getEquipmentScreen() {
		return new EquipmentScreen(this);
	}

	public GalaxyScreen getGalaxyScreen() {
		return new GalaxyScreen(this);
	}

	public LocalScreen getLocalScreen() {
		return new LocalScreen(this);
	}

	public PlanetScreen getPlanetScreen() {
		if (player.getCurrentSystem() == null && player.getHyperspaceSystem() == null) {
			return null;
		}
		return new PlanetScreen(this);
	}
	
	public HyperspaceScreen getLaunchScreen() {
		return new HyperspaceScreen(this, false);
	}
	
	public DiskScreen getDiskScreen() {
		return new DiskScreen(this);
	}
		
	public OptionsScreen getOptionsScreen() {
		return new OptionsScreen(this);
	}
		
	public LibraryScreen getLibraryScreen() {
		return new LibraryScreen(this, null);
	}

	public TutorialSelectionScreen getAcademyScreen() {
		return new TutorialSelectionScreen(this);
	}
	
	public HackerScreen getHackerScreen() {
		return new HackerScreen(this);
	}
	
	public NavigationBar getNavigationBar() {
		return navigationBar;
	}
	
	public boolean isHyperspaceTargetValid() {
		if (player.getHyperspaceSystem() == null) {
			return false;
		}
		if (player.getHyperspaceSystem() == player.getCurrentSystem()) {
			return false;
		}
		int distance = player.getCurrentSystem() == null ? computeDistance(player.getPosition(), player.getHyperspaceSystem()) : player.getCurrentSystem().computeDistance(player.getHyperspaceSystem()); 
		if (!Settings.unlimitedFuel && player.getCobra().getFuel() < distance) {
			return false;			
		}
		return true;		
	}

	private int computeDistance(Point p, SystemData system) {
		int dx = p.x - system.getX();
		int dy = p.y - system.getY();
		return (int) Math.sqrt(dx * dx + dy * dy) << 2;		
	}
	
	public boolean performHyperspaceJump() {		
		InGameManager.safeZoneViolated = false;
		if (player.getActiveMissions().size() == 0) {
			player.increaseJumpCounter();
		}
		boolean willEnterWitchSpace = player.getRating().ordinal() > Rating.POOR.ordinal() && Math.random() <= 0.02;
		for (Mission mission: player.getActiveMissions()) {
			willEnterWitchSpace |= mission.willEnterWitchSpace();
		}
		if (player.getCobra().getPitch() <= -2.0f && player.getCobra().getRoll() <= -2.0f) {
			willEnterWitchSpace = true;
		}
		int distance = player.getCurrentSystem() == null ? computeDistance(player.getPosition(), player.getHyperspaceSystem()) : 
				player.getCurrentSystem().computeDistance(player.getHyperspaceSystem());
		if (willEnterWitchSpace) {
			distance >>= 1;
			int x = player.getCurrentSystem() == null ? player.getPosition().x : player.getCurrentSystem().getX();
			int y = player.getCurrentSystem() == null ? player.getPosition().y : player.getCurrentSystem().getY();
		    int dx = player.getHyperspaceSystem().getX() - x;
		    int dy = player.getHyperspaceSystem().getY() - y;
		    int nx = x + (dx >> 1);
		    int ny = y + (dy >> 1);
		    player.setPosition(nx, ny);
		    player.setCurrentSystem(null);
		} else {
			player.setCurrentSystem(player.getHyperspaceSystem());
		}
		player.getCobra().setFuel(player.getCobra().getFuel() - distance);
		FlightScreen fs = new FlightScreen(this, false);
		if (willEnterWitchSpace) {
			fs.enterWitchSpace();
		}
		setScreen(fs);	
    	GLES11.glMatrixMode(GLES11.GL_TEXTURE);
    	GLES11.glLoadIdentity();
		navigationBar.setActiveIndex(2);
		player.setLegalValue(player.getLegalValue() >> 1);
		return true;
	}
	
	public boolean performIntergalacticJump() {
		InGameManager.safeZoneViolated = false;
		if (player.getActiveMissions().size() == 0) {
			player.increaseIntergalacticJumpCounter();
			if (player.getIntergalacticJumpCounter() == 1) {
				// Mimic Amiga behavior: Mission starts after 1 intergal hyperjump
				// and 63 other jumps (intergal or intragal).
				player.resetJumpCounter();
			}
		}
		int nextGal = generator.getCurrentGalaxy() + 1;
		if (nextGal > 8 || nextGal < 1) {
			nextGal = 1;
		}
		generator.buildGalaxy(nextGal);
		player.setCurrentSystem(generator.getSystem(player.getCurrentSystem().getIndex()));
		player.setHyperspaceSystem(player.getCurrentSystem());
		player.getCobra().removeEquipment(EquipmentStore.galacticHyperdrive);
		setIntergalActive(false);
		setScreen(new FlightScreen(this, false));
    	GLES11.glMatrixMode(GLES11.GL_TEXTURE);
    	GLES11.glLoadIdentity();
		navigationBar.setActiveIndex(2);
		return true;
	}
	
	@Override
	protected void saveState() {
		try {
			fileUtils.saveState(getFileIO(), getCurrentScreen());
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
		
	@Override
	public void onPause() {
		try {
			setSaving(true);
			super.onPause();
		} finally {
			setSaving(false);
		}	
		if (getInput() != null) {
			getInput().dispose();
		}
		if (textureManager != null) {
			textureManager.freeAllTextures();
		}
		AliteLog.d("Alite.onPause", "onPause end");
	}

	@Override
	public void onDestroy() {
		if (getCurrentScreen() != null && !(getCurrentScreen() instanceof FlightScreen)) {
			try {
				AliteLog.d("[ALITE]", "Performing autosave.");
				getFileUtils().autoSave(this);
			} catch (Exception e) {
				AliteLog.e("[ALITE]", "Autosaving commander failed.", e);
			}
		}
		AliteLog.e("Alite.OnDestroy", "Destroying Alite...");
		while (saving) {
			AliteLog.e("OnDestroy", "Still saving...");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		AliteLog.e("Alite.OnDestroy", "Destroying Alite Done.");
		super.onDestroy();
	}

	@Override
	public void onStop() {
		AliteLog.e("Alite.OnStop", "Stopping Alite...");
		getInput().dispose();
		while (saving) {
			AliteLog.e("OnStop", "Still saving...");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		AliteLog.e("Alite.OnStop", "Stopping Alite Done.");				
		super.onStop();		
	}

	public synchronized void setSaving(boolean b) {
		saving = b;
	}
	
	public void loadAutosave() {
		try {
			AliteLog.d("[ALITE]", "Loading autosave.");
			getFileUtils().autoLoad(this);
		} catch (Exception e)  {
			AliteLog.e("[ALITE]", "Loading autosave commander failed.", e);
		}		
	}
	
	@Override
	public void onResume() {	
		AliteLog.d("Alite.onResume", "onResume begin");
		if (textureManager != null) {
			textureManager.clear();
		}
		createInputIfNecessary();
		super.onResume();
		AliteLog.d("Alite.onResume", "onResume end");
	}

	@Override
  public void afterSurfaceCreated() {
    navigationBar = new NavigationBar(this);
    navigationBar.add("Launch", Assets.launchIcon, null);
    int intergalId = navigationBar.add("Gal. Jump", Assets.launchIcon, null);
    navigationBar.setVisible(intergalId, false);
    navigationBar.add("Status", Assets.statusIcon, "StatusScreen");
    navigationBar.add("Buy", Assets.buyIcon, "BuyScreen");
		navigationBar.add("Inventory", Assets.inventoryIcon, "InventoryScreen");
		navigationBar.add("Equip", Assets.equipIcon, "EquipmentScreen");
		navigationBar.add("Galaxy", Assets.galaxyIcon, "GalaxyScreen");
		navigationBar.add("Local", Assets.localIcon, "LocalScreen");
		navigationBar.add("Planet", Assets.planetIcon, "PlanetScreen");		
		navigationBar.add("Disk", Assets.diskIcon, "DiskScreen");
		navigationBar.add("Options", Assets.optionsIcon, "OptionsScreen");
		navigationBar.add("Library", Assets.libraryIcon, "LibraryScreen");
		navigationBar.add("Academy", Assets.academyIcon, "AcademyScreen");		
		hackerId = navigationBar.add("Hacker", Assets.hackerIcon, "HackerScreen");
		navigationBar.setVisible(hackerId, false);
    navigationBar.add("Quit", Assets.quitIcon, null);
		navigationBar.setActiveIndex(2);		
		
		AliteFont.ct = new DefaultCoordinateTransformer(this);
		font = new AliteFont(this);
		final float scaleFactor = getScaleFactor();
		Assets.regularFont    = new GLText();
		Assets.boldFont       = new GLText();
		Assets.italicFont     = new GLText();
		Assets.boldItalicFont = new GLText();
		Assets.titleFont      = new GLText(); 
		Assets.smallFont      = new GLText();

		if (AliteConfig.HAS_EXTENSION_APK) {
			Assets.regularFont.load(ObbExpansionsManager.getInstance().getMainRoot() + "assets/robotor.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.boldFont.load(ObbExpansionsManager.getInstance().getMainRoot() + "assets/robotob.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.italicFont.load(ObbExpansionsManager.getInstance().getMainRoot() + "assets/robotoi.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.boldItalicFont.load(ObbExpansionsManager.getInstance().getMainRoot() + "assets/robotobi.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.titleFont.load(ObbExpansionsManager.getInstance().getMainRoot() + "assets/robotor.ttf", (int) (60.0f * scaleFactor), 60, 2, 2);
			Assets.smallFont.load(ObbExpansionsManager.getInstance().getMainRoot() + "assets/robotor.ttf", (int) (30.0f * scaleFactor), 30, 2, 2);
		} else {
			Assets.regularFont.load(getAssets(), "robotor.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.boldFont.load(getAssets(), "robotob.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.italicFont.load(getAssets(), "robotoi.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.boldItalicFont.load(getAssets(), "robotobi.ttf", (int) (40.0f * scaleFactor), 40, 2, 2);
			Assets.titleFont.load(getAssets(), "robotor.ttf", (int) (60.0f * scaleFactor), 60, 2, 2);
			Assets.smallFont.load(getAssets(), "robotor.ttf", (int) (30.0f * scaleFactor), 30, 2, 2);			
		}
	}
	
	@Override
	public void onBackPressed() {
		// Does nothing on purpose: Saves the player from accidentally quitting
		// the application. Later on, we might want to implement a meaningful
		// functionality here....
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (getCurrentScreen() instanceof FlightScreen) {
			FlightScreen fs = (FlightScreen) getCurrentScreen();
			if (fs.getInformationScreen() == null) {
				fs.togglePause();
			}
		}
		invalidateOptionsMenu();
		return true;
	}
	
	public static void setDefiningScreen(AliteScreen definingScreen) {
		Alite.definingScreen = definingScreen;
	}
	
	public static AliteScreen getDefiningScreen() {
		return Alite.definingScreen;
	}
	
	public TextureManager getTextureManager() {
		return textureManager;
	}
	
	public AliteFont getFont() {
		return font;
	}
	
	public void runAsync(final Runnable runnable) {
		((GLSurfaceView) getCurrentView()).queueEvent(runnable);
	}
	
	public void setLaserManager(LaserManager man) {
		this.laserManager = man;
	}
	
	public LaserManager getLaserManager() {
		return laserManager;
	}

	public static Alite get() {
		return alite;
	}

	public void restartMe() {
		runOnUiThread(new Runnable() {
		    public void run() {
		        recreate();
		    }
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		AliteLog.d("ON CONFIGURATION CHANGED", "ON CONFIGURATION CHANGED");
		super.onConfigurationChanged(config);
	}
}
