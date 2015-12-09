package de.phbouillon.android.games.alite.model.generator;

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
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.generator.enums.Economy;
import de.phbouillon.android.games.alite.model.generator.enums.Government;

public class SystemData implements Serializable {
	private static final long serialVersionUID = 6117084866118128888L;

	/**
	 * Pair String for the name generation algorithm; . represents
	 * non-printable characters.
	 */
	private static final String pairString = "..LEXEGEZACEBISOUSESARMAINDIREA.ERATENBERALAVETIEDORQUANTEISRION";
		
	/**
	 * Simple helper method to save some casting: In Java, a character added
	 * to a String will be concatenated to the String, BUT: If _two_
	 * characters are added like this: "String" + c1 + c2, the sum of the
	 * character ASCII values will be added to the String, yielding something
	 * like "String 123". To prevent this, each character is cast using
	 * this method.
	 * 
	 * @param offset value of the character (as integer value to allow for
	 *        specification of the argument without casting).
	 * @return String representation of the character.
	 */
	private static String S(int offset) {
		return "" + (char) offset;
	}

	// The following are "Commands" in the system description generation.
	
	/**
	 * PLANET_NAME will be expanded to the name of the current system.
	 * For example: Lave.
	 */
	private static final String PLANET_NAME = S(0x01);
	
	/**
	 * PLANET_NAME_IAN will be expanded to the name of the current system
	 * plus the string ian.
	 * For example: Lavian.
	 * 
	 * Note that the (original) Text Elite version checked for vowels at
	 * the end of the system name and omitted them, so for the planet
	 * Lave, the result of this command would be Lavian. However, the
	 * Amiga version of Elite did not remove the terminating vowels. We
	 * remove the vowels at the end, because it is easier to read; thus
	 * diverging from the original Amiga version in favor of readability.
	 */
	private static final String PLANET_NAME_IAN = S(0x02);
	
	/**
	 * Returns a random name based on the same algorithm as the
	 * system name generation.
	 * For example: ORERVE.
	 */
	private static final String RANDOM_NAME = S(0x03);
	
	/**
	 * Removes the character before this command, if it is a space.
	 */
	private static final String REMOVE_PREVIOUS_SPACE = S(0x04);
	
	/**
	 * Every letter following a space after this command will be
	 * capitalized until the command END_CAPITALIZATION is reached.
	 */
	private static final String BEGIN_CAPITALIZATION  = S(0x05);
	private static final String END_CAPITALIZATION = S(0x06);
	
	private boolean capitalize = false;
		
	/**
	 * All words from the Amiga version to generate the description texts for 
	 * all systems. I have also included the "dead code" fragments found in the
	 * binary adf.
	 */
	private static final String [][] DESCRIPTION_TEXT_LIST = {
		/* 0x80 */  {"fabled ", "notable ", "well known ", "famous ", "noted "},
		/* 0x81 */	{"very ", "mildly ", "most ", "reasonably ", ""},
		/* 0x82 */	{"ancient ", S(0x97), "great ", "vast ", "pink "},
		/* 0x83 */	{BEGIN_CAPITALIZATION + S(0x9C) + " " + S(0x9B) + END_CAPITALIZATION + "plantations ", "mountains ", S(0x9A), S(0xa5) + "forests ", "oceans "},
		/* 0x84 */  {S(0xA6), "mountain ", "edible ", "tree ", "spotted "},
		/* 0x85 */  {S(0x9D), S(0x9E), S(0x86) + REMOVE_PREVIOUS_SPACE + "oid ", S(0xA4), S(0xA3)},
		/* 0x86 */  {"walking " + S(0x8D), "crab ", "bat ", "lobst ", RANDOM_NAME},
		/* 0x87 */  {"ancient ", "exceptional ", "eccentric ", "ingrained ", S(0x97)},
		/* 0x88 */	{"shyness ", "silliness ", "mating traditions ", "loathing of " + S(0x89), "love for " + S(0x89)},
		/* 0x89 */	{"food blenders ", "tourists ", "poetry ", "discos ", S(0x91)},
		/* 0x8A */	{"its " + S(0x82) + S(0x83), "the " + PLANET_NAME_IAN + S(0x84) + S(0x85), "its inhabitants' " + S(0x87) + S(0x88), S(0x9F) + END_CAPITALIZATION, "its " + S(0x90) + S(0x91)},
		/* 0x8B */	{"beset ", "plagued ", "ravaged ", "cursed ", "scourged "},
		/* 0x8C */	{S(0x96) + "civil war", S(0x8D) + S(0x84) + S(0x85) + REMOVE_PREVIOUS_SPACE + "s ", "a " + S(0x8D) + "disease ", S(0x96) + "earthquakes ", S(0x96) + "solar activity "},
		/* 0x8D */	{"killer ", "deadly ", "evil ", "lethal ", "vicious "},
		/* 0x8E */	{"juice ", "brandy ", "water ", "brew ", "gargle blasters "},
		/* 0x8F */	{RANDOM_NAME, PLANET_NAME_IAN + S(0x85), PLANET_NAME_IAN + RANDOM_NAME, PLANET_NAME_IAN + S(0x8D), S(0x8D) + RANDOM_NAME},
		/* 0x90 */	{"fabulous ", "exotic ", "hoopy ", "unusual ", "exciting "},
		/* 0x91 */	{"cuisine ", "night life ", "casinos ", "sitcoms ", S(0x9F) + END_CAPITALIZATION},
		/* 0x92 */	{PLANET_NAME, "The planet " + PLANET_NAME, "The world " + PLANET_NAME, "This planet ", "This world "},
		/* 0x93 */	{S(0x81) + S(0x80) + "for " + S(0x8A), S(0x81) + S(0x80) + "for " + S(0x8A) + "and " + S(0x8A), S(0x8B) + "by " + S(0x8C), S(0x81) + S(0x80) + "for " + S(0x8A) + "but is " + S(0x8B) + "by " + S(0x8C), "a " + S(0x94) + S(0x95)},
		/* 0x94 */	{REMOVE_PREVIOUS_SPACE + "n unremarkable ", "boring ", "dull ", "tedious ", "revolting "},
		/* 0x95 */	{"planet ", "world ", "place ", "little planet ", "dump "},
		/* 0x96 */	{"frequent ", "occasional ", "unpredictable ", "dreadful ", "deadly "},
		/* 0x97 */	{"funny ", "weird ", "unusual ", "strange ", "peculiar "},
/*DeadCode 0x98 */  {"son of a bitch ", "scoundrel ", "blackguard ", "rogue ", "whoreson beetle headed flap ear'd knave"},
/*DeadCode 0x99 */  {"", "", "", "", ""},
		/* 0x9A */	{"parking meters ", "dust clouds ", "icebergs ", "rock formations ", "volcanoes "},
		/* 0x9B */	{"plant ", "tulip ", "banana ", "corn ", "weed "},
		/* 0x9C */	{RANDOM_NAME, PLANET_NAME_IAN + RANDOM_NAME, PLANET_NAME_IAN + S(0x8D), "inhabitant ", PLANET_NAME_IAN + RANDOM_NAME},
		/* 0x9D */	{"shrew ", "beast ", "bison ", "snake ", "wolf "},
		/* 0x9E */	{"leopard ", "cat ", "monkey ", "goat ", "fish "},
		/* 0x9F */	{BEGIN_CAPITALIZATION + S(0x8F) + S(0x8E), PLANET_NAME_IAN + BEGIN_CAPITALIZATION + S(0x9D) + S(0xA0), "its " + BEGIN_CAPITALIZATION + S(0x90) + S(0x9E) + S(0xA0), S(0xA1) + S(0xA2), BEGIN_CAPITALIZATION + S(0x8F) + S(0x8E)},
		/* 0xA0 */	{"meat ", "cutlet ", "steak ", "burgers ", "soup "},
		/* 0xA1 */	{"ice ", "mud ", "zero-G ", "vacuum ", PLANET_NAME_IAN + "ultra "},
		/* 0xA2 */	{"hockey ", "cricket ", "karate ", "polo ", "tennis "},
		/* 0xA3 */	{"wasp ", "moth ", "grub ", "ant ", RANDOM_NAME},
		/* 0xA4 */	{"poet ", "arts graduate ", "yak ", "snail ", "slug "},
		/* 0xA5 */  {"dense ", "lush ", "rain ", "bamboo ", "deciduous "},
		/* 0xA6 */  {"green ", "black ", "yellow stripey ", "pinky grey ", "white "},		
	};
	
	int                     index;         // 0-255
	int                     x;             // 0-255
	int                     y;             // 0-255
	Economy                 economy;       // 0-7
	Government              govType;       // 0-7
	int                     techLevel;     // 0-15 I think (original source says 0-16, which I doubt)
							               //      The value will be increased by 1 before printing,
	                                       //      so 16 is the max tech level (and 1 the least).
	int                     population;    // 0-255
	int                     productivity;  // 0-65535
	int                     diameter;      // 0-65535
	int                     fuelPrice;     // 1-27 -- 32?!?!	
 	FastSeedType            goatSoupSeed;
	String                  name;
	String                  inhabitants;
	String                  inhabitantCode;
	String                  description;

	int                     planetTexture; // Index of the pre-generated planet texture
	int                     ringsTexture;  // Index of the ring texture or 0 for no rings
	int                     cloudsTexture; // Index of the cloud texture or 0 for no clouds
	int                     starTexture;   // 0-22 O-M class stars with 3 different luminosity settings,
	                                       // and two dwarf types.
	int                     dockingFee;    // Fee for automatic docking, if no docking computer is
	                                       // installed. 400-3300.
	
	final List <SystemData> reachableSystems = new ArrayList<SystemData>();
				
	public static SystemData createSystem(int index, SeedType seed) {
		SystemData result = new SystemData();
		
		result.index = index;
		
		result.computePosition(seed);
		result.computeGovernment(seed);
		result.computeEconomy(seed);
		result.computeTechLevel(seed);
		result.computePopulation(seed);
		result.computeProductivity(seed);
		result.computeDiameter(seed);
		
		// Initialize goat soup seed:
		char seedA = (char) (seed.getWord(0) ^ seed.getWord(1));
		result.goatSoupSeed = new FastSeedType(seedA, (char) (seedA ^ seed.getWord(2)));

		result.inhabitants = InhabitantComputation.computeInhabitantString(seed, result);
		result.name = StringUtil.readableCase(result.generateRandomName(seed));		
		result.computeDescriptionString(seed);
		
		// The fuel price is fixed for a given system and must be
		// computed AFTER the planet description (because the seed
		// is twisted four times during the description generation.
		// If computed earlier, the fuel prices won't match the
		// Amiga version).
		result.computeFuelPrice(seed);
		
		result.computeTextures(seed);
		result.computeDockingFee(seed);
		
		return result;
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "SystemData " + this, e);
			throw(e);
		}
    }

	private void computePosition(SeedType seed) {
		x = (seed.getHiByte(1) * 0x00FF) / 0x00FF;
		y = (seed.getHiByte(0) * 0x007F) / 0x00FF;
	}
	
	private void computeGovernment(SeedType seed) {
		govType = Government.values()[seed.shiftRight(1, 3) & 7];	
	}
	
	private void computeEconomy(SeedType seed) {
		int economyValue = seed.shiftRight(0, 8) & 7;
		if (govType.ordinal() <= 1) {
			economyValue |= 2;
		}
		economy = Economy.values()[economyValue]; 
	}
	
	private void computeTechLevel(SeedType seed) {
		techLevel = (seed.shiftRight(1, 8) & 3) + (economy.ordinal() ^ 7);
		techLevel += (govType.ordinal() >> 1) + 1;
		if ((govType.ordinal() & 1) == 1) {
			techLevel++;
		}		
	}
	
	private void computePopulation(SeedType seed) {
		population  = seed.getLoByte(0) & 0x3f;
		population += 1; 		
	}
	
	private void computeProductivity(SeedType seed) {
		productivity  = seed.getLoByte(0) & 0x3f; 
		productivity += 1;
		productivity *= 0x1b;
		productivity /= 5;		
	}
	
	private void computeDiameter(SeedType seed) {
		diameter = (seed.getWord(0) & 0x7fff) + 0x3a98; // Min diameter = 15.000 (0x3A98)	
	}
	
	private void computeFuelPrice(SeedType seed) {
		fuelPrice = (0x001F & seed.getLoByte(0)) + 1;
	}
	
	private void computeTextures(SeedType seed) {
		planetTexture = ((char) (seed.getLoByte(0)) + (char) (seed.getHiByte(1))) % 64;
		ringsTexture  = seed.getLoByte(1) < 128 ? (seed.getLoByte(1) % 15) + 1 : 0;
		cloudsTexture = seed.getHiByte(2) % 9;
		starTexture   = (seed.getLoByte(2) + seed.getHiByte(1)) % 21 + (seed.getHiByte(0) > 240 ? 2 : seed.getHiByte(0) > 220 ? 1 : 0);
	}
	
	private void computeDockingFee(SeedType seed) {
		dockingFee = (8 - govType.ordinal()) * 50;
	}
	
	/**
	 * Modifies a String containing word pairs like "a evil" to read "an evil".
	 * Note that this will also incorrectly alter "a universe" to "an universe",
	 * but since this is not a construct possible from the goat soup string, we
	 * get away with it...
	 */
	private String correctAAn(String temp) {
		String [] words = temp.split(" ");
		StringBuffer result = new StringBuffer();
		for (int i = 0, n = words.length; i < n; i++) {
			result.append(words[i]);
			if (words[i].equalsIgnoreCase("a") && i < (n - 1)) {
				char nextChar = Character.toLowerCase(words[i + 1].charAt(0));
				if (nextChar == 'a' || nextChar == 'e' || nextChar == 'i' || nextChar == 'o' || nextChar == 'u') {
					result.append("n");
				}
			}
			result.append(" ");
		}
		return result.toString().trim();
	}

	private void computeDescriptionString(SeedType seed) {
		capitalize = false;		
		String temp = computeGoatSoup(END_CAPITALIZATION + S(0x92) + "is " + S(0x93) + REMOVE_PREVIOUS_SPACE + ".").
						replaceAll(" +", " ");
		// This call certainly lacks style (it is plain ugly), but it works :)
		// I use the removeAdditionalWhitespaces method to get rid of strings as
		// "The planet xy is a n unremarkable planet" (note the extra 
		// space between a and n).
		// After that, "a" is corrected to "an", where necessary.
		description = correctAAn(StringUtil.removeAdditionalWhitespaces(temp));
	}
	
	// Goat soup description string generation
	
	private char generateRandomNumber() {
		char d0 = goatSoupSeed.b();
		char d1 = goatSoupSeed.a();
		goatSoupSeed.setA(d0);
		d0 += d1;
		goatSoupSeed.setB(d0);
		d0 &= 0xFF;
		return d0;
	}
	
	private void tweakSeed(SeedType seed) {
		char temp;
		temp = (char) (seed.getWord(0) + seed.getWord(1) + seed.getWord(2));
		seed.setWord(0, seed.getWord(1));
		seed.setWord(1, seed.getWord(2));
		seed.setWord(2, temp);
	}
	
	public String generateRandomName(SeedType nameSeed) {
		char longNameFlag = (char) (nameSeed.getWord(0) & 64);
		
		char [] pair = new char [4];
		for (int i = 0; i < 4; i++) {
			pair[i] = (char) (2 * (nameSeed.shiftRight(2, 8) & 31));
			tweakSeed(nameSeed);
		}
	
		char [] pairs = pairString.toCharArray();
		StringBuilder resultStringBuilder = new StringBuilder();
		for (int i = 0; i < (longNameFlag > 0 ? 4 : 3); i++) {
			resultStringBuilder.append(pairs[pair[i]]);
			resultStringBuilder.append(pairs[pair[i] + 1]);
		}
		
		return resultStringBuilder.toString().replaceAll("\\.", "");
	}

	private void appendPrintableCharacter(StringBuilder builder, char c) {
		if (capitalize && (builder.length() == 0 || builder.charAt(builder.length() - 1) == ' ')) {
			c = Character.toUpperCase(c);
		}
		builder.append(c);		
	}
	
	private void appendGoatSoupString(StringBuilder builder, char c) {
		int rnd = generateRandomNumber() / 52; // [0..255] / 52 = [0..4] 
		builder.append(computeGoatSoup(DESCRIPTION_TEXT_LIST[c - 0x80][rnd]));		
	}
	
	private void appendCommand(StringBuilder builder, char c) {
		switch (c) {
			case 0x01: // <Planet name>
				       builder.append(StringUtil.readableCase(name));
					   builder.append(" ");
					   break;					   
			case 0x02: // <Planet name>ian (omit vowels at the end of the planet name)
					   char lastChar = Character.toLowerCase(name.charAt(name.length() - 1));
				       if (lastChar == 'a' || lastChar == 'e' || lastChar == 'i' || lastChar == 'o' || lastChar == 'u') {
				    	   builder.append(StringUtil.readableCase(name).substring(0, name.length() - 1));
				       } else {
				    	   builder.append(StringUtil.readableCase(name));
				       }
					   builder.append("ian ");
					   break;
			case 0x03: // <Random name>
				       SeedType localSeed = new SeedType(goatSoupSeed.a(), 
											             goatSoupSeed.b(),
											             (char) (goatSoupSeed.a() ^ goatSoupSeed.b()));
					   builder.append(StringUtil.readableCase(generateRandomName(localSeed)));
					   builder.append(" ");
					   break;
			case 0x04: // Deletes last space or appends "*" to the string if the string is empty.
				       // The "*" will later be removed along with all preceding spaces.
					   if (builder.length() > 0 && builder.charAt(builder.length() - 1) == ' ') {
						   builder.deleteCharAt(builder.length() - 1);
					   } else if (builder.length() == 0) {
						   builder.append("*");
					   }
					   break;
			case 0x05: // Turns on capitalization. All following words will have an upper case first character.
					   capitalize = true;
			           break;
			case 0x06: // Turns off capitalization.
					   capitalize = false;
					   break;
		    default:   // Whoops. Wrong op code. Issue error and continue.
		    	       System.err.println("Invalid command code " + (int) c + " -- ignoring.");
		    	       break;
		}		
	}
	
	private void appendCommandFlag(StringBuilder builder, char c) {
		if (c >= 0x80) {
			// Some goat soup string
			appendGoatSoupString(builder, c);
		} else {
			// Command
			appendCommand(builder, c);
		}
	}
	
	private String computeGoatSoup(String source) {
		if (source == null || source.length() == 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();		
		int index = 0;		
		do {
			char c = source.charAt(index);
			if (c < 0x80 && c >= 0x10) {
				appendPrintableCharacter(builder, c);
			} else {
				appendCommandFlag(builder, c);
			}
			index++;			
		} while (index < source.length());
	
		return builder.toString();
	}
	
		
	public void computeReachableSystems(SystemData [] allSystems) {
		// Computes all reachable planets. (I.e. all planets with a
		// distance up to 7.0 light years).
		
		for (SystemData data: allSystems) {
			int dx = x - data.x;
			int dy = y - data.y;
			int dist = (int) Math.sqrt(dx * dx + dy * dy) << 2;
			
			if (dist <= 70) {
				reachableSystems.add(data);
			}
		}
	}
	
	public int computeDistance(SystemData targetSystem) {
		int dx = x - targetSystem.x;
		int dy = y - targetSystem.y;
		return (int) Math.sqrt(dx * dx + dy * dy) << 2;		
	}
	
	// Pretty print the system data.
	public String toString() {
		String info = String.format(Locale.getDefault(), "%10s (%3d, %3d) TL: %2d %22s %22s %32s Prod: %8s Diameter: %8d km Pop: %8s %s", 
				name,
				(int) x,
				(int) y,
				(int) techLevel,
				economy,
				govType,
				inhabitants,
				getGnp(),
				(int) diameter,
				getPopulation(),
				description);
		
		StringBuilder systems = new StringBuilder(" [");
		for (SystemData data: reachableSystems) {
			if (data.index == index) {
				continue;
			}
			systems.append(data.name + ", ");
		}
		if (systems.length() > 2) {
			systems.deleteCharAt(systems.length() - 1);
			systems.deleteCharAt(systems.length() - 1);			
		} else {
			systems.append("--");
		}
		systems.append("]");
		
		return info + systems;
	}
	
	// Getter Methods
	
	public Economy getEconomy() {
		return economy;
	}
	
	public Government getGovernment() {
		return govType;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public void setNewPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	
	public String getInhabitants() {
		return inhabitants;
	}
	
	public int getTechLevel() {
		return techLevel;
	}
	
	public String getGnp() {
		return productivity / 10 + "." + (productivity % 10) + " MCr";
	}
	
	public String getPopulation() {
		return population / 10 + "." + (population % 10) + " bn";
	}
	
	public int getDiameter() {
		return diameter;
	}
	
	public String getDescription() {
		return description;
	}
	
	public SystemData [] getReachableSystems() {
		return reachableSystems.toArray(new SystemData[0]);
	}
	
	public int getFuelPrice() {
		return fuelPrice;
	}
	
	public int getPlanetTexture() {
		return planetTexture;
	}
	
	public int getRingsTexture() {
		return ringsTexture;
	}
	
	public int getCloudsTexture() {
		return cloudsTexture;
	}
	
	public String getInhabitantCode() {
		return inhabitantCode;
	}
	
	public int getStarTexture() {
	    return starTexture;
	}

	public long getStationHandsDockingFee() {
		return dockingFee;
	}
}
