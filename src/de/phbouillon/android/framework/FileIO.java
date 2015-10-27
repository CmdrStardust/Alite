package de.phbouillon.android.framework;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {
	public InputStream readFile(String fileName) throws IOException;
	public OutputStream writeFile(String fileName) throws IOException;
	public OutputStream appendFile(String fileName) throws IOException;
	public long fileLastModifiedDate(String fileName) throws IOException;
	public boolean exists(String fileName) throws IOException;
	public boolean mkDir(String fileName) throws IOException;
	public byte [] readFileContents(String fileName) throws IOException;
	public byte [] readPartialFileContents(String fileName, int length) throws IOException;
	public byte [] readPartialFileContents(String fileName, int offset, int length) throws IOException;
	public byte [] readFileContents(String fileName, int fromOffset) throws IOException;
	public File [] getFiles(String directory, String fileNamePattern) throws IOException;
	public boolean deleteFile(String fileName) throws IOException;
	public boolean copyFile(String srcFileName, String dstFileName) throws IOException;
	public void zip(String zipName, String ...fileNames) throws IOException;
	public void unzip(File zipFile, File targetDirectory) throws IOException;
	
	public InputStream readPrivateFile(String fileName) throws IOException;
	public String getPrivatePath(String fileName) throws IOException;
	public boolean existsPrivateFile(String fileName) throws IOException;
}
