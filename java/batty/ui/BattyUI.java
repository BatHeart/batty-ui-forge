/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batty.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
//import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Handles Coordinate, Direction and Timer operation & display, and maintains
 * the user options files
 * 
 * @author BatHeart
 * @version 1.8.8 (1.10.0)
 */
public class BattyUI extends Gui {

	private static Minecraft mc;
	//private static GuiIngame gui = new GuiIngame(mc);
	
	int showCoords = 1;
	boolean shadedCoords = true;
	boolean hideTimer = false;
	boolean shadedTimer = true;
	boolean timerRunning = false;
	boolean toggleTimer = false;
	boolean resetTimer = false;
	boolean shadedFPS = true;
	boolean hideFPS = false;
	boolean coordsCopyTPFormat = false;

	int myTitleText = 0xFF8800; // default textcolour = oldgold
	int myPosCoordText = 0x55FFFF; // default poscoordscolour = aqua
	int myNegCoordText = 0xCCFFFF; // default negcoordscolour = coolblue
	int myPosChunkText = 0xFFFFFF; // default poschunkcolour = white
	int myNegChunkText = 0xAAAAAA; // default negchunkcolour = grey
	int myCompassText = 0xFF8800; // default compasscolour = oldgold
	int myChevronText = 0x55FFFF; // default textcolour = aqua
	int myBiomeText = 0xAAAAAA; // default biomecolour = grey
	int myRectColour = 0x55555555;
	int myTimerStopText = 0xFF8800; // default timer stop colour = oldgold
	int myTimerRunText = 0x55FFFF; // default timer running colour = aqua
	int myFPSText = 0x55FFFF; // default fps colour = aqua
	int myPosX;
	int myPosY;
	int myPosZ;
	boolean myXminus;
	boolean myZminus;	
	int myAngle;
	int myDir;
	int myMoveX;
	int myMoveZ;
	int myFind;

	protected static final ResourceLocation batUIResourceLocation = new ResourceLocation("battyUI:textures/batheart_icon.png");
	
	static float batLogoScaler = 0.036F;
	static int batLogoU = 0;
	static int batLogoV = 0;
	static int batLogoX = (int) (256.0F * batLogoScaler);
	static int batLogoY = (int) (256.0D * batLogoScaler);
	
	int coordLocation = 0;

	int myXLine, myYLine, myZLine, myBiomeLine;
	int myBaseOffset;
	int myCoord0Offset, myCoord1Offset, myCoord2Offset;
	int myRHSlocation;
	int coordBoxW, coordBoxH;
	int coordBoxL, coordBoxR, coordBoxTop, coordBoxBase;

	int timerLocation = 2;
	int clockBoxW, clockBoxH, clockBoxL, clockBoxR, clockBoxTop, clockBoxBase;
	int myTimerLine, myTimerOffset;
	
	int fpsLocation = 1;
	int fpsBoxW, fpsBoxH, fpsBoxL, fpsBoxR, fpsBoxTop, fpsBoxBase;
	int myFPSLine, myFPSOffset;

	private String myChevronUp = "+"; // default 'increasing coordinates'
										// character
	private String myChevronDown = "-"; // default 'decreasing coordinates'
										// character
	private static final String[] myCardinalPoint = new String[] { "N", "NE",
			"E", "SE", "S", "SW", "W", "NW" };
	private static final String[] myColourList = new String[] { "black",
			"darkblue", "darkgreen", "darkaqua", "darkred", "purple", "brown",
			"grey", "darkgrey", "blue", "green", "aqua", "sage", "violet",
			"orange", "lime", "silver", "coolblue", "red", "gold", "oldgold",
			"lightpurple", "pink", "yellow", "white" };
	private static final int[] myColourCodes = { 0x000000, 0x0000AA, 0x00AA00,
			0x00AAAA, 0xAA0000, 0xAA00AA, 0xAA5500, 0xAAAAAA, 0x555555,
			0x5555FF, 0x55FF55, 0x55FFFF, 0x88AA00, 0x8855CC, 0xCC5500,
			0xCCFF00, 0xCCCCCC, 0xCCFFFF, 0xFF5555, 0xFFAA00, 0xFF8800,
			0xFF55FF, 0xFFAAAA, 0xFFFF55, 0xFFFFFF };
	private File optionsFile;
	private File runtimeFile;
	private int secondCounter = 0;
	private int minuteCounter = 0;
	private int hourCounter = 0;
	private int tickCounter = 0;
	Properties propts = new Properties();
	Properties proprt = new Properties();

	/**
	 * Constructor for BattyUI: also handles reading of option files
	 * 
	 * @param par1Minecraft
	 *            Instance of Minecraft, giving access to variables and methods
	 */
	public BattyUI(Minecraft par1Minecraft) {
		this.mc = par1Minecraft;

		this.optionsFile = new File(this.mc.mcDataDir, "BatMod.properties");
		this.runtimeFile = new File(this.mc.mcDataDir, "BatMod.runtime");

		this.retrieveOptions();
		this.retrieveRuntimeOptions();
	}

	/**
	 * Draws a rectangle of the texture provided at the location specified, sized and scaled as specified
	 */
	public void drawTexture(int x, int y, int u, int v, int width,
			int height, ResourceLocation resourceLocation, float scaler)

	{
		x = (int) (x / scaler);
		y = (int) (y / scaler);

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glScalef(scaler, scaler, scaler);

		mc.renderEngine.bindTexture(resourceLocation);
		this.drawTexturedModalRect(x, y, u, v, width, height);

		GL11.glPopMatrix();
	}
/**
 * Calls drawTexture() to present the BatHeart Logo at the screen position specified
 * @param x - location across screen from left to right
 * @param y - location down screen from top to bottom
 */
	protected void drawLogoTexture(int x, int y) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(255.0F, 255.0F, 255.0F, 255.0F);

		drawTexture(x, y, batLogoU, batLogoV, (int) (batLogoX / batLogoScaler),
				(int) (batLogoY / batLogoScaler), batUIResourceLocation,
				batLogoScaler);

		GL11.glDisable(GL11.GL_BLEND);
	}
	
	
	/**
	 * Searches for 'name' within the array 'names'
	 * 
	 * @param names
	 *            Array of String to be searched
	 * @param name
	 *            String that we expect to find
	 * @return Integer element where match found, or -1 if no match
	 */
	private static int nameSearch(String[] names, String name) {
		for (int n = 0; n < names.length; n++) {
			if (names[n].equals(name)) {
				return n;
			}
		}
		return -1;
	}

	/**
	 * Given a 360-degree compass bearing, converts it to an integer relating to
	 * one of the 8 cardinal points of the compass
	 * 
	 * @param par0
	 *            Floating point 360-degree compass bearing
	 * @return integer compass direction (0=North, 1=North-East etc)
	 */
	private int getCardinalPoint(float par0) {
		double myPoint;
		myPoint = MathHelper.wrapAngleTo180_float(par0) + 180D;
		myPoint += 22.5D;
		myPoint %= 360D;
		myPoint /= 45D;
		return MathHelper.floor_double(myPoint);
	}

	/**
	 * Converts the Minecraft.showCoords variable into a String ready for
	 * writing to options file
	 * 
	 * @return a String containing value from "0" to "4"
	 */
	private String constructCoordVisString() {
		String var1 = "";
		var1 = var1 + this.showCoords;
		return var1;
	}

	/**
	 * Converts the Minecraft.coordLocation variable into a String ready for
	 * writing to options file
	 * 
	 * @return a String containing value from "0" to "3"
	 */
	private String constructCoordLocString() {
		String var1 = "";
		var1 = var1 + this.coordLocation;
		return var1;
	}

	/**
	 * Converts the Minecraft.hideTimer variable into a string ready for writing
	 * to options file
	 * 
	 * @return a String containing either "true" or "false"
	 */
	private String constructTimerVisString() {
		String var1;
		if (this.hideTimer) {
			var1 = "false";
		} else {
			var1 = "true";
		}
		return var1;
	}

	/**
	 * Converts the Minecraft.timerRunning variable into a string ready for
	 * writing to options file
	 * 
	 * @return a String containing either "true" or "false"
	 */
	private String constructTimerRunString() {
		String var1;
		if (this.timerRunning) {
			var1 = "true";
		} else {
			var1 = "false";
		}
		return var1;
	}

	/**
	 * Converts the Minecraft.hideFPS variable into a String ready for
	 * writing to options file
	 * 
	 * @return a String containing either "true" or "false"
	 */
	private String constructFPSVisString() {
		String var1;
		if (this.hideFPS) {
			var1 = "false";
		} else {
			var1 = "true";
		}
		return var1;
	}

	/**
	 * Converts the Minecraft.fpsLocation variable into a String ready for
	 * writing to options file
	 * 
	 * @return a String containing value from "0" to "4"
	 */
	private String constructFPSLocString() {
		String var1 = "";
		var1 = var1 + this.fpsLocation;
		return var1;
	}

	
	/**
	 * Converts the Minecraft.timerLocation variable into a String ready for
	 * writing to options file
	 * 
	 * @return a String containing value from "0" to "3"
	 */
	private String constructTimerLocString() {
		String var1 = "";
		var1 = var1 + this.timerLocation;
		return var1;
	}

	/**
	 * Takes the saved Timer value String (from the options file) and splits it
	 * into integer hour, minute and second parts
	 * 
	 * @param var1
	 *            The Timer value String
	 */
	private void parseTimeString(String var1) {
		Logger.getLogger("Minecraft").info(var1);
		String[] var2 = var1.split("\\|");
		this.hourCounter = Integer.parseInt(var2[0]);
		this.minuteCounter = Integer.parseInt(var2[1]);
		this.secondCounter = Integer.parseInt(var2[2]);
	}

	/**
	 * Constructs a Time String from the hourCounter, minuteCounter and
	 * secondCounter variables
	 * 
	 * @return the Time as a String in "00:00:00" format
	 */
	private String constructTimeString() {
		String var1 = "";
		var1 = var1 + (this.hourCounter >= 10 ? "" : "0");
		var1 = var1 + this.hourCounter;
		var1 = var1 + ":";
		var1 = var1 + (this.minuteCounter >= 10 ? "" : "0");
		var1 = var1 + this.minuteCounter;
		var1 = var1 + ":";
		var1 = var1 + (this.secondCounter >= 10 ? "" : "0");
		var1 = var1 + this.secondCounter;
		return var1;
	}

	/**
	 * Reformats the Time String ready to be written away to the options file
	 * 
	 * @return Time String, reformatted with "|" delimiters instead of ":"
	 */
	private String getSaveString() {
		return this.constructTimeString().replace(":", "|");
	}

	/**
	 * Sets all of the Timer variables back to zero, and resets the resetTimer
	 * flag
	 */
	private void resetTimer() {
		this.resetTimer = false;
		this.tickCounter = this.hourCounter = this.minuteCounter = this.secondCounter = 0;

		this.storeRuntimeOptions();
	}

	/**
	 * Increments the Timer by 1 second, handling overflow to minutes and hours
	 * as necessary
	 */
	private void addOneSecond() {
		++this.secondCounter;

		if (this.secondCounter >= 60) {
			this.secondCounter -= 60;
			++this.minuteCounter;
		}

		if (this.minuteCounter >= 60) {
			this.minuteCounter -= 60;
			++this.hourCounter;
		}
	}

	/**
	 * Handles Timer activities - resets, stop/starts and general running
	 * 
	 * @param var1
	 *            The in-game client-side tick counter (int)
	 */
	public void updateTimer(int var1) {
		if (this.resetTimer) {
			this.resetTimer();
		}
		if (this.toggleTimer) {
			this.toggleTimer = false;
			this.tickCounter = 0;
			this.timerRunning = !this.timerRunning;
			this.storeRuntimeOptions();
		}
		if (this.timerRunning) {
			if (this.tickCounter == 0) {
				this.tickCounter = var1;
			}

			if (var1 - this.tickCounter >= 20) {
				this.addOneSecond();
				this.tickCounter += 20;
			}
		}
	}

	/**
	 * Handles the retrieval, interpretation and storage of all options from the
	 * BatMod.properties file
	 */
	private void retrieveOptions() {

		if (this.optionsFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(this.optionsFile);
				try {
					propts.load(fis);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} catch (FileNotFoundException var5) {
				var5.printStackTrace();
			}
		}

		String myShade = propts.getProperty("Coords.shade");
		String myTxtChr1 = propts.getProperty("Coords.chars.Increase");
		String myTxtChr2 = propts.getProperty("Coords.chars.Decrease");
		String myTxtCol1 = propts.getProperty("Coords.colours.TitleText");
		String myTxtCol6 = propts.getProperty("Coords.colours.CoordText");
		String myTxtCol2 = propts.getProperty("Coords.colours.PosCoordText");
		String myTxtCol7 = propts.getProperty("Coords.colours.NegCoordText");
		String myTxtCol3 = propts.getProperty("Coords.colours.CompassText");
		String myTxtCol4 = propts.getProperty("Coords.colours.ChevronText");
		String myTxtCol5 = propts.getProperty("Coords.colours.BiomeText");
		String myTxtCol8 = propts.getProperty("Coords.colours.PosChunkText");
		String myTxtCol9 = propts.getProperty("Coords.colours.NegChunkText");
		String myTxtFmt1 = propts.getProperty("Coords.copy.tpFormat");
		//String myTxtLoc1 = propts.getProperty("Coords.layout.ScreenPosition");

		if (myShade != null) {
			this.shadedCoords = myShade.equals("true");
		}
		if (myTxtChr1 != null) {
			if (myTxtChr1.length() > 1) {
				myChevronUp = myTxtChr1.substring(0, 1);
			} else {
				myChevronUp = myTxtChr1;
			}
		}
		if (myTxtChr2 != null) {
			if (myTxtChr2.length() > 1) {
				myChevronDown = myTxtChr2.substring(0, 1);
			} else {
				myChevronDown = myTxtChr2;
			}
		}

		if (myTxtCol1 != null) {
			myFind = nameSearch(myColourList, myTxtCol1);
			if (myFind != -1) {
				myTitleText = myColourCodes[myFind];
			}
		}

		if (myTxtCol2 != null) {
			myFind = nameSearch(myColourList, myTxtCol2);
			if (myFind != -1) {
				myPosCoordText = myColourCodes[myFind];
			}
		}
		if (myTxtCol7 != null) {
			myFind = nameSearch(myColourList, myTxtCol7);
			if (myFind != -1) {
				myNegCoordText = myColourCodes[myFind];
			}
		}
		if (myTxtCol3 != null) {
			myFind = nameSearch(myColourList, myTxtCol3);
			if (myFind != -1) {
				myCompassText = myColourCodes[myFind];
			}
		}
		if (myTxtCol4 != null) {
			myFind = nameSearch(myColourList, myTxtCol4);
			if (myFind != -1) {
				myChevronText = myColourCodes[myFind];
			}
		}
		if (myTxtCol5 != null) {
			myFind = nameSearch(myColourList, myTxtCol5);
			if (myFind != -1) {
				myBiomeText = myColourCodes[myFind];
			}
		}
		if (myTxtCol6 != null) {
			myFind = nameSearch(myColourList, myTxtCol6);
			if (myFind != -1) {
				myPosCoordText = myColourCodes[myFind];
				myNegCoordText = myColourCodes[myFind];
			}
		}
		if (myTxtCol8 != null) {
			myFind = nameSearch(myColourList, myTxtCol8);
			if (myFind != -1) {
				myPosChunkText = myColourCodes[myFind];
			}
		}
		if (myTxtCol9 != null) {
			myFind = nameSearch(myColourList, myTxtCol9);
			if (myFind != -1) {
				myNegChunkText = myColourCodes[myFind];
			}
		}
		if (myTxtFmt1 != null) {
			this.coordsCopyTPFormat = myTxtFmt1.equals("true");
		}
		
		
		myShade = propts.getProperty("Timer.shade");
		myTxtCol1 = propts.getProperty("Timer.colours.Stopped");
		myTxtCol2 = propts.getProperty("Timer.colours.Running");
		//myTxtLoc1 = propts.getProperty("Timer.layout.ScreenPosition");
		if (myShade != null) {
			this.shadedTimer = myShade.equals("true");
		}
		if (myTxtCol1 != null) {
			myFind = nameSearch(myColourList, myTxtCol1);
			if (myFind != -1) {
				myTimerStopText = myColourCodes[myFind];
			}
		}
		if (myTxtCol2 != null) {
			myFind = nameSearch(myColourList, myTxtCol2);
			if (myFind != -1) {
				myTimerRunText = myColourCodes[myFind];
			}
		}

		myShade = propts.getProperty("FPS.shade");
		myTxtCol1 = propts.getProperty("FPS.colours.Text");
		//myTxtLoc1 = propts.getProperty("FPS.layout.ScreenPosition");
		if (myShade != null) {
			this.shadedFPS = myShade.equals("true");
		}
		if (myTxtCol1 != null) {
			myFind = nameSearch(myColourList, myTxtCol1);
			if (myFind != -1) {
				myFPSText = myColourCodes[myFind];
			}
		}
	
	}

	/**
	 * Handles retrieval, interpretation and storage of the saved game data from
	 * the BatMod.runtime file
	 */
	private void retrieveRuntimeOptions() {

		if (this.runtimeFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(this.runtimeFile);
				try {
					proprt.load(fis);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} catch (FileNotFoundException var5) {
				var5.printStackTrace();
			}
		}
		String myTimeString = proprt.getProperty("Timer.saved");
		if (myTimeString != null) {
			this.parseTimeString(myTimeString);
		}
		String myCoordsVis = proprt.getProperty("Coords.visible");
		if (myCoordsVis != null) {
			this.showCoords = Integer.parseInt(myCoordsVis);
		}
		String myTimerVis = proprt.getProperty("Timer.visible");
		if (myTimerVis != null) {
			this.hideTimer = !myTimerVis.equals("true");
		}
		String myCoordsLoc = proprt.getProperty("Coords.location");
		if (myCoordsLoc != null) {
			this.coordLocation = Integer.parseInt(myCoordsLoc);
		}
		String myTimerLoc = proprt.getProperty("Timer.location");
		if (myTimerLoc != null) {
			this.timerLocation = Integer.parseInt(myTimerLoc);
		}
		String myTimerRuns = proprt.getProperty("Timer.running");
		if (myTimerRuns != null) {
			this.timerRunning = myTimerRuns.equals("true");
		}
		String myFPSVis = proprt.getProperty("FPS.visible");
		if (myFPSVis != null) {
			this.hideFPS = !myFPSVis.equals("true");
		}
		String myFPSLoc = proprt.getProperty("FPS.location");
		if (myFPSLoc != null) {
			this.fpsLocation = Integer.parseInt(myFPSLoc);
		}
	}

	/**
	 * Handles writing away the game data to the BatMod.runtime file
	 */
	private void storeRuntimeOptions() {

		proprt.setProperty("Timer.saved", this.getSaveString());
		proprt.setProperty("Coords.visible", this.constructCoordVisString());
		proprt.setProperty("Timer.visible", this.constructTimerVisString());
		proprt.setProperty("Coords.location", this.constructCoordLocString());
		proprt.setProperty("Timer.location", this.constructTimerLocString());
		proprt.setProperty("Timer.running", this.constructTimerRunString());
		proprt.setProperty("FPS.visible", this.constructFPSVisString());
		proprt.setProperty("FPS.location", this.constructFPSLocString());
		try {
			FileOutputStream fos = new FileOutputStream(this.runtimeFile);
			proprt.store(fos, null);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the player's Coordinates, Compass Bearing, Movement Indicators,
	 * Biome etc. onto the game screen Handles which elements are to be
	 * displayed, and adjusts screen location accordingly
	 */
	private void renderPlayerCoords() {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		FontRenderer var8 = this.mc.fontRendererObj;
		ScaledResolution myRes = new ScaledResolution(this.mc);
		BlockPos myBlock = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);		
		myPosX = MathHelper.floor_double(this.mc.thePlayer.posX);
		myXminus = (this.mc.thePlayer.posX < 0);		
		myPosY = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minY);
		myPosZ = MathHelper.floor_double(this.mc.thePlayer.posZ);
		myZminus = (this.mc.thePlayer.posZ < 0);		
		myAngle = getCardinalPoint(this.mc.thePlayer.rotationYaw);
		myDir = MathHelper
				.floor_double((double) (this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		//myMoveX = Direction.offsetX[myDir];
		//myMoveZ = Direction.offsetZ[myDir];

		// Coord display modes beyond basic require more space
		int compassW = this.mc.fontRendererObj.getStringWidth(myCardinalPoint[7]);
		if (this.showCoords > 2) {
			coordBoxW = 104;
			coordBoxH = 40;
		} else {
			coordBoxW = 80;
			coordBoxH = 30;
		}
		// screen locations
		switch (this.coordLocation) {
		case 0:
			// top left positioning
			coordBoxR = coordBoxW + 1;
			coordBoxBase = coordBoxH + 1;
			break;
		case 1:
			// top right positioning
			coordBoxR = myRes.getScaledWidth() - 1;
			coordBoxBase = coordBoxH + 1;
			break;

		case 2:
			// Bottom Right Positioning
			coordBoxR = myRes.getScaledWidth() - 1;
			coordBoxBase = myRes.getScaledHeight() - 1;
			break;

		case 3:
			// bottom left positioning ** not permitted **
			coordBoxR = coordBoxW + 1;
			coordBoxBase = myRes.getScaledHeight() - 1;
			break;
		}

		coordBoxL = coordBoxR - coordBoxW;
		coordBoxTop = coordBoxBase - coordBoxH;
		myXLine = coordBoxTop + 1;
		myYLine = myXLine + 10;
		myZLine = myYLine + 10;
		myBiomeLine = myZLine + 10;
		myBaseOffset = coordBoxL + 1;
		myCoord0Offset = myBaseOffset + 10;		
		myCoord1Offset = myBaseOffset + 16;
		myCoord2Offset = myBaseOffset + 39;
		if (this.showCoords > 2) {
			//myRHSlocation = myBaseOffset + 77;
			myRHSlocation = coordBoxR - compassW - 1;
		} else {
			myRHSlocation = myBaseOffset + 64;
		}

		if (this.shadedCoords) {
			drawRect((int) coordBoxL, coordBoxTop, coordBoxR, coordBoxBase,
					myRectColour);
		}

		var8.drawStringWithShadow(String.format("x: "), myBaseOffset, myXLine,
				myTitleText);
		var8.drawStringWithShadow(String.format("y: "), myBaseOffset, myYLine,
				myTitleText);
		var8.drawStringWithShadow(String.format("z: "), myBaseOffset, myZLine,
				myTitleText);

		if (this.showCoords < 4) {
			if (!myXminus) {
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(myPosX) }),
						myCoord1Offset, myXLine, myPosCoordText);
			} else {
				var8.drawStringWithShadow("-", myCoord0Offset, myXLine, myNegCoordText);
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(Math.abs(myPosX)) }),
						myCoord1Offset, myXLine, myNegCoordText);
			}
			var8.drawStringWithShadow(String.format("%d",
					new Object[] { Integer.valueOf(myPosY) }), myCoord1Offset,
					myYLine, myPosCoordText);
			if (!myZminus) {
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(myPosZ) }),
						myCoord1Offset, myZLine, myPosCoordText);
			} else {
				var8.drawStringWithShadow("-", myCoord0Offset, myZLine, myNegCoordText);
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(Math.abs(myPosZ)) }),
						myCoord1Offset, myZLine, myNegCoordText);
			}
		} else {
			if (myPosX >= 0) {
				var8.drawStringWithShadow(
						String.format("c%d ",
								new Object[] { Integer.valueOf(myPosX >> 4) }),
						myCoord2Offset, myXLine, myPosChunkText);
				var8.drawStringWithShadow(
						String.format("b%d",
								new Object[] { Integer.valueOf(myPosX & 15) }),
						myCoord1Offset, myXLine, myPosChunkText);
			} else {
				var8.drawStringWithShadow(
						String.format("c%d ",
								new Object[] { Integer.valueOf(myPosX >> 4) }),
						myCoord2Offset, myXLine, myNegChunkText);
				var8.drawStringWithShadow(
						String.format("b%d",
								new Object[] { Integer.valueOf(myPosX & 15) }),
						myCoord1Offset, myXLine, myPosChunkText);
			}
			var8.drawStringWithShadow(String.format("%d",
					new Object[] { Integer.valueOf(myPosY) }), myCoord1Offset,
					myYLine, myPosCoordText);
			if (myPosZ >= 0) {
				var8.drawStringWithShadow(
						String.format("c%d ",
								new Object[] { Integer.valueOf(myPosZ >> 4) }),
						myCoord2Offset, myZLine, myPosChunkText);
				var8.drawStringWithShadow(
						String.format("b%d",
								new Object[] { Integer.valueOf(myPosZ & 15) }),
						myCoord1Offset, myZLine, myPosChunkText);
			} else {
				var8.drawStringWithShadow(
						String.format("c%d ",
								new Object[] { Integer.valueOf(myPosZ >> 4) }),
						myCoord2Offset, myZLine, myNegChunkText);
				var8.drawStringWithShadow(
						String.format("b%d",
								new Object[] { Integer.valueOf(myPosZ & 15) }),
						myCoord1Offset, myZLine, myPosChunkText);
			}
		}
		
		
		
		drawLogoTexture((myRHSlocation - 12), (myYLine-1));
				
		var8.drawStringWithShadow(myCardinalPoint[myAngle], myRHSlocation,
				myYLine, myCompassText);

		if (this.showCoords > 1) {
			switch (myAngle) {
			case 0:
				var8.drawStringWithShadow(myChevronDown + myChevronDown,
						myRHSlocation, myZLine, myNegCoordText);
				break;
			case 1:
				var8.drawStringWithShadow(myChevronDown, myRHSlocation,
						myZLine, myNegCoordText);
				var8.drawStringWithShadow(myChevronUp, myRHSlocation, myXLine,
						myPosCoordText);
				break;
			case 2:
				var8.drawStringWithShadow(myChevronUp + myChevronUp,
						myRHSlocation, myXLine, myPosCoordText);
				break;
			case 3:
				var8.drawStringWithShadow(myChevronUp, myRHSlocation, myXLine,
						myPosCoordText);
				var8.drawStringWithShadow(myChevronUp, myRHSlocation, myZLine,
						myPosCoordText);
				break;
			case 4:
				var8.drawStringWithShadow(myChevronUp + myChevronUp,
						myRHSlocation, myZLine, myPosCoordText);
				break;
			case 5:
				var8.drawStringWithShadow(myChevronUp, myRHSlocation, myZLine,
						myPosCoordText);
				var8.drawStringWithShadow(myChevronDown, myRHSlocation,
						myXLine, myNegCoordText);
				break;
			case 6:
				var8.drawStringWithShadow(myChevronDown + myChevronDown,
						myRHSlocation, myXLine, myNegCoordText);
				break;
			case 7:
				var8.drawStringWithShadow(myChevronDown, myRHSlocation,
						myXLine, myNegCoordText);
				var8.drawStringWithShadow(myChevronDown, myRHSlocation,
						myZLine, myNegCoordText);
				break;
			}
		}
		if (this.showCoords > 2) {
			if (this.mc.theWorld != null
					&& this.mc.theWorld.isBlockLoaded(myBlock)) {
				Chunk myChunk = this.mc.theWorld.getChunkFromBlockCoords(myBlock);
				var8.drawStringWithShadow(
						myChunk.getBiome(myBlock,
								this.mc.theWorld.getWorldChunkManager()).biomeName,
						myBaseOffset, myBiomeLine, myBiomeText);
			}
		}
		if (this.showCoords == 3) {
			// show minecraft day
			var8.drawStringWithShadow(String.format("dy "), myCoord2Offset, myYLine,
					myTitleText);
			var8.drawStringWithShadow(String.format("%d", new Object[] { Long
					.valueOf(this.mc.theWorld.getWorldTime() / 24000L) }),
					myCoord2Offset + 14, myYLine, myBiomeText);
		}		

	}

	

	/**
	 * Writes the Timer String up onto the game screen
	 */
	private void renderPlayerTimer() {

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		ScaledResolution myRes = new ScaledResolution(this.mc);
		String myTime = this.constructTimeString();
		int timeStringWid = this.mc.fontRendererObj.getStringWidth(myTime);
		clockBoxW = 12 + timeStringWid;
		clockBoxH = 10;

		// screen locations
		switch (this.timerLocation) {
		case 0:
			// top left positioning
			clockBoxR = clockBoxW + 1;
			clockBoxBase = clockBoxH + 1;
			if (this.coordLocation == 0) {
				clockBoxBase += (coordBoxH + 1);
			}
			break;
		case 1:
			// top centre positioning
			clockBoxR = (myRes.getScaledWidth() / 2) + (clockBoxW / 2);
			clockBoxBase = clockBoxH + 1;
			break;
			
		case 2:
			// top right positioning
			clockBoxR = myRes.getScaledWidth() - 1;
			clockBoxBase = clockBoxH + 1;
			if (this.coordLocation == 1) {
				clockBoxBase += (coordBoxH + 1);
			}
			break;

		case 3:
			// bottom right positioning
			clockBoxR = myRes.getScaledWidth() - 1;
			clockBoxBase = myRes.getScaledHeight() - 1;
			if (this.coordLocation == 2) {
				clockBoxBase -= (coordBoxH + 1);
			}
			break;

		case 4:
			// Bottom left Positioning
			clockBoxR = clockBoxW + 1;
			clockBoxBase = myRes.getScaledHeight() - 15;
			if (this.coordLocation == 3) {
				clockBoxBase -= (coordBoxH + 1);
			}
			break;
		}
		clockBoxL = clockBoxR - clockBoxW;
		clockBoxTop = clockBoxBase - clockBoxH;

		myTimerLine = clockBoxTop + 1;
		myTimerOffset = clockBoxL + 6;

		if (this.shadedTimer) {
			drawRect((int) clockBoxL, clockBoxTop, clockBoxR, clockBoxBase,
					myRectColour);

		}
		if (this.timerRunning) {
			this.mc.fontRendererObj.drawStringWithShadow(myTime, myTimerOffset,
					myTimerLine, myTimerRunText);
		} else {
			this.mc.fontRendererObj.drawStringWithShadow(myTime, myTimerOffset,
					myTimerLine, myTimerStopText);
		}
	}

	/**
	 * Writes the FPS string up onto the game screen
	 */
	private void renderPlayerFPS() {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		ScaledResolution myRes = new ScaledResolution(this.mc);
		String myFPS = mc.debug.split(" ")[0] + " FPS";
		int fpsStringWid = this.mc.fontRendererObj.getStringWidth(myFPS);
		fpsBoxW = 12 + fpsStringWid;
		fpsBoxH = 10;
		
		// screen locations
		switch (this.fpsLocation) {
		case 0:
			// top left positioning
			fpsBoxR = fpsBoxW + 1;
			fpsBoxBase = fpsBoxH + 1;
			if (this.timerLocation == 0) {
				fpsBoxBase += (clockBoxH + 1);
			}
			if (this.coordLocation == 0) {
				fpsBoxBase += (coordBoxH + 1);
			}

			break;
		case 1:
			// top centre positioning
			fpsBoxR = (myRes.getScaledWidth() / 2) + (fpsBoxW / 2);
			fpsBoxBase = fpsBoxH + 1;
			if (this.timerLocation == 1) {
				fpsBoxBase += (clockBoxH + 1);
			}
			break;
			
		case 2:
			// top right positioning
			fpsBoxR = myRes.getScaledWidth() - 1;
			fpsBoxBase = fpsBoxH + 1;
			if (this.timerLocation == 2) {
				fpsBoxBase += (clockBoxH + 1);
			}
			if (this.coordLocation == 1) {
				fpsBoxBase += (coordBoxH + 1);
			}
			break;

		case 3:
			// bottom right positioning
			fpsBoxR = myRes.getScaledWidth() - 1;
			fpsBoxBase = myRes.getScaledHeight() - 1;
			if (this.timerLocation == 3) {
				fpsBoxBase -= (clockBoxH + 1);
			}
			if (this.coordLocation == 2) {
				fpsBoxBase -= (coordBoxH + 1);
			}
			break;

		case 4:
			// Bottom left Positioning
			fpsBoxR = fpsBoxW + 1;
			fpsBoxBase = myRes.getScaledHeight() - 15;
			if (this.timerLocation == 4) {
				fpsBoxBase -= (clockBoxH + 1);
			}
			if (this.coordLocation == 3) {
				fpsBoxBase -= (coordBoxH + 1);
			}
			break;
		}
		
		fpsBoxL = fpsBoxR - fpsBoxW;
		fpsBoxTop = fpsBoxBase - fpsBoxH;

		myFPSLine = fpsBoxTop + 1;
		myFPSOffset = fpsBoxL + 6;

		if (this.shadedFPS) {
			drawRect((int) fpsBoxL, fpsBoxTop, fpsBoxR, fpsBoxBase,
					myRectColour);
		}
		
		this.mc.fontRendererObj.drawStringWithShadow(myFPS, myFPSOffset,
				myFPSLine, myFPSText);
		
		
		
	}
	
	
	
	/**
	 * Changes the coordinate display view by scrolling through the different
	 * options one by one Also stores the current option into the BatMod.runtime
	 * file
	 */
	public void hideUnhideCoords() {
		this.showCoords += 1;
		if (this.showCoords > 4) {
			this.showCoords = 0;
		}
		this.storeRuntimeOptions();
		BattyUIKeys.keyToggleCoords = false;
	}

	/**
	 * Moves the position that the coordinates appear in on-screen between three
	 * corners
	 */
	public void rotateScreenCoords() {
		this.coordLocation += 1;
		if (this.coordLocation > 2) {
			this.coordLocation = 0;
		}
		this.storeRuntimeOptions();
		BattyUIKeys.keyMoveCoords = false;
	}
/**
 * Copies the current XYZ coordinates as a string into the System Clipboard for
 * pasting wherever the player wishes
 */
	public void copyScreenCoords() {
		StringSelection myCoordString;
		if (this.coordsCopyTPFormat) {
			myCoordString = new StringSelection(myPosX + " " + myPosY + " "	+ myPosZ);
		} else {
			myCoordString = new StringSelection("x:" + myPosX + " y:" + myPosY	+ " z:" + myPosZ);
		}
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(myCoordString, null);

		BattyUIKeys.keyCopyCoords = false;
	}
		
	/**
	 * Toggles the Timer visibility on and off Also stores the current option
	 * into the BatMod.runtime file
	 */
	public void hideUnhideStopWatch() {
		this.hideTimer = !this.hideTimer;
		this.storeRuntimeOptions();
		BattyUIKeys.keyToggleTimerVis = false;
	}

	/**
	 * Moves the position that the timer appears in on-screen between the four
	 * corners and the top centre
	 */
	public void rotateScreenTimer() {
		this.timerLocation += 1;
		if (this.timerLocation > 4) {
			this.timerLocation = 0;
		}
		this.storeRuntimeOptions();
		BattyUIKeys.keyMoveTimer = false;
	}

	/**
	 * Toggles the FPS visibility on and off 
	 * Also stores the current option into the BatMod.runtime file
	 */
	public void hideUnhideFPS() {
		this.hideFPS = !this.hideFPS;
		this.storeRuntimeOptions();
		BattyUIKeys.keyToggleFPSVis = false;
	}

	/**
	 * Moves the position that the fps appears in on-screen between the four
	 * corners and the top centre
	 */
	public void rotateScreenFPS() {
		this.fpsLocation += 1;
		if (this.fpsLocation > 3) {
			this.fpsLocation = 0;
		}
		this.storeRuntimeOptions();
		BattyUIKeys.keyMoveFPS = false;
	}
	
	/**
	 * Publicly exposed method, handles Coordinate and Timer rendering when they
	 * are intended to appear
	 */
	@SubscribeEvent
	public void renderPlayerInfo(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.HOTBAR) {
			return;
		}

		if (BattyUIKeys.keyToggleCoords) {
			this.hideUnhideCoords();
		}

		if (BattyUIKeys.keyMoveCoords) {
			this.rotateScreenCoords();
		}
		
		if (BattyUIKeys.keyCopyCoords) {
			this.copyScreenCoords();
		}

		if (BattyUIKeys.keyToggleTimerVis) {
			this.hideUnhideStopWatch();
		}

		if (BattyUIKeys.keyToggleTimerRun) {
			this.toggleTimer = true;
			BattyUIKeys.keyToggleTimerRun = false;
		}

		if (BattyUIKeys.keyResetTimer) {
			this.resetTimer = true;
			BattyUIKeys.keyResetTimer = false;
		}

		if (BattyUIKeys.keyMoveTimer) {
			this.rotateScreenTimer();
		}
		
		if (BattyUIKeys.keyToggleFPSVis) {
			this.hideUnhideFPS();
		}
		
		if (BattyUIKeys.keyMoveFPS) {
			this.rotateScreenFPS();
		}

		this.updateTimer(this.mc.ingameGUI.getUpdateCounter());

		if (!this.mc.gameSettings.showDebugInfo) {
			if (this.showCoords > 0) {
				this.renderPlayerCoords();
			} else {
				this.coordBoxH = 0;
			}
			if (this.hideTimer) {
				this.clockBoxH = 0;
			} else {
				this.renderPlayerTimer();
			}
			if (!this.hideFPS) {
				this.renderPlayerFPS();
			}
		}

	}
@SubscribeEvent
public void stopTheClock(GuiOpenEvent event){
	if (event.gui instanceof GuiMainMenu)
	this.timerRunning = false;
	storeRuntimeOptions();
}

}
