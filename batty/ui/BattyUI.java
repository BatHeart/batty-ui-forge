/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batty.ui;

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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * @author BatHeart
 */
public class BattyUI extends Gui {

	private final Minecraft mc;
	private GuiIngame gui;
	int showCoords = 1;
	boolean shadedCoords = true;
	boolean hideTimer = false;
	boolean shadedTimer = true;
	boolean timerRunning = false;
	boolean toggleTimer = false;
	boolean resetTimer = false;

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
	int myPosX;
	int myPosY;
	int myPosZ;
	int myAngle;
	int myDir;
	int myMoveX;
	int myMoveZ;
	int myFind;
	int myXLine;
	int myYLine;
	int myZLine;
	int myBiomeLine;
	int myBaseOffset;
	int myCoord1Offset;
	int myCoord2Offset;
	int myXtender;
	int myZtender;
	int myRHSlocation;
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

	public BattyUI(Minecraft par1Minecraft) {
		this.mc = par1Minecraft;

		this.gui = this.mc.ingameGUI;

		this.optionsFile = new File(this.mc.mcDataDir, "BatMod.properties");
		this.runtimeFile = new File(this.mc.mcDataDir, "BatMod.runtime");

		this.retrieveOptions();
		this.retrieveRuntimeOptions();
	}

	private static int nameSearch(String[] names, String name) {
		for (int n = 0; n < names.length; n++) {
			if (names[n].equals(name)) {
				return n;
			}
		}
		return -1;
	}

	private int getCardinalPoint(float par0) {
		double myPoint;
		myPoint = MathHelper.wrapAngleTo180_float(par0) + 180D;
		myPoint += 22.5D;
		myPoint %= 360D;
		myPoint /= 45D;
		return MathHelper.floor_double(myPoint);
	}

	private String constructCoordVisString() {
		String var1 = "";
		var1 = var1 + this.showCoords;
		return var1;
	}

	private String constructTimerVisString() {
		String var1;
		if (this.hideTimer) {
			var1 = "false";
		} else {
			var1 = "true";
		}
		return var1;
	}

	private void parseTimeString(String var1) {
		Logger.getLogger("Minecraft").info(var1);
		String[] var2 = var1.split("\\|");
		this.hourCounter = Integer.parseInt(var2[0]);
		this.minuteCounter = Integer.parseInt(var2[1]);
		this.secondCounter = Integer.parseInt(var2[2]);
	}

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

	private String getSaveString() {
		return this.constructTimeString().replace(":", "|");
	}

	private void resetTimer() {
		this.resetTimer = false;
		this.tickCounter = this.hourCounter = this.minuteCounter = this.secondCounter = 0;

		this.storeRuntimeOptions();
	}

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
		myShade = propts.getProperty("Timer.shade");
		myTxtCol1 = propts.getProperty("Timer.colours.Stopped");
		myTxtCol2 = propts.getProperty("Timer.colours.Running");
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

	}

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

	}

	private void storeRuntimeOptions() {

		proprt.setProperty("Timer.saved", this.getSaveString());
		proprt.setProperty("Coords.visible", this.constructCoordVisString());
		proprt.setProperty("Timer.visible", this.constructTimerVisString());

		try {
			FileOutputStream fos = new FileOutputStream(this.runtimeFile);
			proprt.store(fos, null);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void renderPlayerCoords() {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		FontRenderer var8 = this.mc.fontRenderer;

		myPosX = MathHelper.floor_double(this.mc.thePlayer.posX);
		myPosY = MathHelper.floor_double(this.mc.thePlayer.posY);
		myPosZ = MathHelper.floor_double(this.mc.thePlayer.posZ);
		myAngle = getCardinalPoint(this.mc.thePlayer.rotationYaw);
		myDir = MathHelper
				.floor_double((double) (this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		myMoveX = Direction.offsetX[myDir];
		myMoveZ = Direction.offsetZ[myDir];

		// screen locations
		myXLine = 2;
		myYLine = 12;
		myZLine = 22;
		myBiomeLine = 32;
		myBaseOffset = 2;
		myCoord1Offset = 12;
		myCoord2Offset = 35;
		if (this.showCoords > 2) {
			myXtender = 30;
			myZtender = 10;
		} else {
			myXtender = 0;
			myZtender = 0;
		}

		if (this.showCoords > 3) {
			myRHSlocation = 78;
		} else {
			myRHSlocation = 58;
		}

		if (this.shadedCoords) {

			drawRect((int) 1, 1, (71 + myXtender), (31 + myZtender),
					myRectColour);

		}

		var8.drawStringWithShadow(String.format("X: "), myBaseOffset, myXLine,
				myTitleText);
		var8.drawStringWithShadow(String.format("Y: "), myBaseOffset, myYLine,
				myTitleText);
		var8.drawStringWithShadow(String.format("Z: "), myBaseOffset, myZLine,
				myTitleText);
		if (this.showCoords < 4) {
			if (myPosX >= 0) {
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(myPosX) }),
						myCoord1Offset, myXLine, myPosCoordText);
			} else {
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(myPosX) }),
						myCoord1Offset, myXLine, myNegCoordText);
			}
			var8.drawStringWithShadow(String.format("%d",
					new Object[] { Integer.valueOf(myPosY) }), myCoord1Offset,
					myYLine, myPosCoordText);
			if (myPosZ >= 0) {
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(myPosZ) }),
						myCoord1Offset, myZLine, myPosCoordText);
			} else {
				var8.drawStringWithShadow(
						String.format("%d",
								new Object[] { Integer.valueOf(myPosZ) }),
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
					new Object[] { Integer.valueOf(myPosY) }), 12, myYLine,
					myPosCoordText);
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
		var8.drawStringWithShadow(myCardinalPoint[myAngle], myRHSlocation,
				myYLine, myCompassText);
		// var8.drawStringWithShadow(String.format(Direction.directions[myDir].substring(0,
		// 1)), 62, 12, 0xFF8800);

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
		// ItemStack var10 = new ItemStack(Item.compass);
		// itemRenderer.renderItemAndEffectIntoGUI(var8, this.mc.renderEngine,
		// var10, 66, 2);
		if (this.showCoords > 2) {
			if (this.mc.theWorld != null
					&& this.mc.theWorld.blockExists(myPosX, myPosY, myPosZ)) {
				Chunk var26 = this.mc.theWorld.getChunkFromBlockCoords(myPosX,
						myPosZ);
				var8.drawStringWithShadow(var26.getBiomeGenForWorldCoords(
						myPosX & 15, myPosZ & 15,
						this.mc.theWorld.getWorldChunkManager()).biomeName,
						myBaseOffset, myBiomeLine, myBiomeText);
			}
		}

	}

	private void renderPlayerTimer() {

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		ScaledResolution myRes = new ScaledResolution(this.mc.gameSettings,
				this.mc.displayWidth, this.mc.displayHeight);
		String myTime = this.constructTimeString();
		float timeStringWid = (float) this.mc.fontRenderer
				.getStringWidth(myTime);
		float timeStringWider = 12.0F + timeStringWid;
		float startClockLocn = (float) (myRes.getScaledWidth())
				- timeStringWider;
		startClockLocn += 5.0F;
		timeStringWid = (float) this.mc.fontRenderer.getStringWidth(myTime);
		if (this.shadedTimer) {
			drawRect((int) (startClockLocn - 6.0F), 1, (int) (startClockLocn
					+ timeStringWid + 6.0F), 11, 0x88555555);

		}
		if (this.timerRunning) {
			this.mc.fontRenderer.drawStringWithShadow(myTime,
					(int) startClockLocn, 2, myTimerRunText);
		} else {
			this.mc.fontRenderer.drawStringWithShadow(myTime,
					(int) startClockLocn, 2, myTimerStopText);
		}
	}

	public void hideUnhideCoords() {
		this.showCoords += 1;
		if (this.showCoords > 4) {
			this.showCoords = 0;
		}
		this.storeRuntimeOptions();
		BattyUIKeys.keyToggleCoords = false;
	}

	public void hideUnhideStopWatch() {
		this.hideTimer = !this.hideTimer;
		this.storeRuntimeOptions();
		BattyUIKeys.keyToggleTimerVis = false;
	}

	@SubscribeEvent
	public void renderPlayerInfo(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}

		if (BattyUIKeys.keyToggleCoords) {
			this.hideUnhideCoords();
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

		this.updateTimer(this.mc.ingameGUI.getUpdateCounter());

		if (!this.mc.gameSettings.showDebugInfo) {
			if (this.showCoords > 0) {
				this.renderPlayerCoords();
			}
			if (!this.hideTimer) {
				this.renderPlayerTimer();
			}
		}

	}
}
