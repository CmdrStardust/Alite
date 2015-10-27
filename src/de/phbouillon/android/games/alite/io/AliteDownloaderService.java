package de.phbouillon.android.games.alite.io;

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

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class AliteDownloaderService extends DownloaderService {
    public static final String BASE64_PUBLIC_KEY = 
    		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoLx78w" +
    		"DAmTRvskWJQfeFHCc/xmuL/rtF2ePkU9xl0yyc/672HtLMQlZZ" +
    		"ZeYqnw5sa+LcBL1+1M59NruKeU9bcugosQm0Ha+gspNLl52utj" +
    		"GcGdGew2+c3u98ZlQt5u5pk4jLEDNL9qWsUHBitZ9EKdhmjt4G" +
    		"c2tlElitFmNYqBxIj7iRb5GJeQlaBmxSflIztPH8W3eApn2FIe" +
    		"2DVS8OQASUGmRuIds76bf+H4pMw5efVWIy1dh7KvgvObDgxMK0" +
    		"Twi2DcndaF0BIsq10kM5Ainii2JiyJtUzSXYRYacIn4KhgmhPv" +
    		"W2iygN3Y0yDbb+GPQB96cJ/SWqlo/xyXzh3QIDAQAB";
    
    public static final byte [] SALT = new byte []
    	{ -31, -5, 7, -31, 3, 127, -1, 91, 77, -4, 11, 3, -1, 13, 114, -97, -13, 15, -12, 81 };

    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return AliteAlarmReceiver.class.getName();
    }
}