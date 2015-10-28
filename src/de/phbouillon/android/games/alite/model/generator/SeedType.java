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

public class SeedType {
	// Three words (16 bytes, ranging from 0-65535 each).
	private char [] word = new char[3]; 
	
	public SeedType() {
		word[0] = 0;
		word[1] = 0;
		word[2] = 0;
	}
	
	public SeedType(char w0, char w1, char w2) {
		this.word[0] = w0;
		this.word[1] = w1;
		this.word[2] = w2;
	}
	
	private char rotateLeft(char x) {
		return (char) (2 * (x & 127) + ((x & 128) >> 7));
	}
	
	private char twist(char x) {
		return (char) ((256 * rotateLeft((char) (x >> 8))) +
							  rotateLeft((char) (x & 255)));		
	}
	public void multiplyByTwo() {
		for (int i = 0; i < 3; i++) {
			word[i] = twist(word[i]);
		}
	}
		
	public char getLoByte(int index) {
		return (char) (word[index] & 255);
	}
	
	public char getHiByte(int index) {
		return (char) (word[index] >> 8);
	}
	
	public char shiftRight(int index, int amount) {
		return (char) (word[index] >> amount);
	}
	
	public char shiftLeft(int index, int amount) {
		return (char) (word[index] << amount);
	}
	
	public char getWord(int index) {
		return word[index];
	}
	
	public void setWord(int index, char value) {
		word[index] = value;
	}
	
	public String toString() {
		return String.format("%02x %02x %02x %02x %02x %02x", word[0] >> 8, word[0] & 0xFF,
														word[1] >> 8, word[1] & 0xFF,
														word[2] >> 8, word[2] & 0xFF);
	}
}
