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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import de.phbouillon.android.framework.FileIO;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.AliteStartManager;
import de.phbouillon.android.games.alite.ScreenBuilder;
import de.phbouillon.android.games.alite.model.CommanderData;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.InventoryItem;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.LegalStatus;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.Rating;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.generator.GalaxyGenerator;
import de.phbouillon.android.games.alite.model.generator.Raxxla;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;

public class FileUtils {
	private static final String [] keys = new String [] {
        "JMz343q8dmb~yC5UUTf8w151GY99P15=iZ687O(Ae8473L39iz6x468l",
        "3+6~1cIWu2jdjP-OaxIq5W#nAf28YF{5H5Dgtf}1u64HfBrVyD8>8t{2",
        "Sn}6HeBJ13pJ&P0J5z1UY63GA1e8ni13O5yMw4FN2p1nIQx925WfE7f_",
        "n15645c3tfh8ah144YZ60WF3W0AL5A01qCZ#s0d9wq3t1zJbwsbX87N6",
        "MJo5i1K5MbpW15jN374M2zw}5l7B11zq^9%2S(j'HJ=|~>NT2S:9xD)n",
        ")?=}]+m@{D@fL 2E<[8':;>[7.>[Cz16=N##4<,qN.]7~S8@571QV>x<",
        "n*3).jH-e.9)K3A%7o8;701bj17&G5Vh)&4z8jxx6_ n%F&+]5,'[7:F",
        "R'+-4wf6)1Q?%%)3m2]&@]K]&%X*+W_#(*'1^;+o)!X@;2$O[&r9=Q!2",
        "[)1#qy)=2Q>u[72++20~x'^7F.8F^YE/+]91\"2{+#473E-Y:-+{>:;:[",
        "<{q:I](Y5,;o-|_d(_32%.rO3.,;5!b_52#t[:3?/B\"'{>[oR_41[64;",
        "~,2[{8M*_)}.=N[T3MJ21jTTxZ531btUlXJ48U4ZXfje58YGKJ2RB60x",
        "B1m3S4vuC43q,-01nO6A377d~[)L#(Y!'_2(1)0W0\"~9@X8.;<(_';$5",
        "9)|]4^86F|($,!%-%k\"8}t\"&>!&1[5:czdkt{ccH,1;nKISF&268J/cB",
        "rfp|4:G)HMEVhRpOqt.L.|=krD1v6xgD13Q>*PvxQ}!:4n2J)]=\":*5_",
        ".3bBXoQ3_=|\"{i}1+5@f~rG7[~]+F)]\",.E;LO( .%O2]^)@Y<4r-F!8",
        "6)@5/%59}7\"\")rN[.-.F&h=.;^'[%W->{<:|/i2'4{&8tG<u'\"=_G17m",
        "zaObfFPL>cb3t7jOk4O7ustpbZtVmkhDPafLmsmYbjggoF&RoVHutBTG",
        "rwNJ3D%qZ^.]1l/@9;&)$1~(G_N,[<|F4/%<';,)_r4D#3&.oS!>(@)^",
        "O#/7,=-?46c#zcm0h6-(9L;/iiMtpW6}w8722JO:#<26:6O1$51. {}7",
        "+G<>1t+O[9NW0250k^8T52D8u913>7]!7y=+|'ri[Se^7h@xq*$M{#H_",
        "5YP*@,~3(G_1#i}\"x796s,xd&&I:)^#8oFV1/o=6v$3XWxVy77KerEU4",
        "Xjhp8tw%RBk3>;G8LzDMFq2eQ0dwvdP7trrDc]sS48,$L3\"u26{*<Z3M",
        "3|Ys>u2mb8),{:2 M!;%?~+5('>77^ D7.3:R@*#?f558~5xToMYx8G]",
        "ElBOBh)g;<AvPpYOK%61cou4^^MhYzCjsc3)NNUjDQ1P]T5YzqxpwCV2",
        "Cj<xo83O n8%7)<_$<_,[]h]7$H:0-@6]*/,1p5@;>&) >/< )l0&f(k",
        " }>{4'~Pl|wkU]D81Mv!F4s65TL@a:m-Uv[1*K_565RuHSt'IL~D|W!R",
        "b|WTV/1O8;}zc7B9QM;l+QwA.aRdp4AK8Jc!Hs8nwQg6O4qxg*l2v8lq",
        "n57ukdlvPaY2uEuzbg*u7@9Owq_0TUm/v8fY7OCS18q-f+CcS8bO!@6x",
        "_q9?g8b!-YG9wV#Met%.X.8_orih%XEvB5cQbI8=gv/H3P3_=O7Z.7Z;",
        "4*c*Z=CnUY8#%x8V7/py.1QY+ln1n27Qfejl2_qfli+ee*DTdlo.Sy/D",
        "wEC-dY/posjlOWq-fCFm#9=.iUXCN8;3ff*x=2JViU@9dpEF5zqPsjM+",
        "KDcIA;b3u2F6h%?0=7Dt_Hs%QQIlS4L!SCm@ZBUl=JPxjUwB4*W8fq#C",
        "ck#6**k*rJofcEa#mpz9%?f_rG4ciZnyLje*KkwDvZGZ3RvzlfV@@EKn",
        "E!P5IIeOl*U=xl4C%wNp-GPahE#CEx8GY@7OV@?n!-3#;2C/xT;R#rz.",
        "2-XEQbN_DNr61p3Bswu/ZYd2C#c8zqO3N9jgq@Wqy6+6KSVHm!nS;0vd",
        "isCZ/cTk/+s%GXi+ZDXqlurwlUW4D_6jNF..-;rWnZ6XwcBOjz%=cF%-",
        "AS31GmhCDI1AZk/cjBDL+UJQSHUaD@HHKJ.sQ*PA%4TnyA*/znhaC4B2",
        "Jni.RG.NBs+PmdLt8CSGBsoRRUyng3GfISj0dR6yvFmFNwkWb+pAl?0n",
        ".R3_fi4VmfGT=hPrSgX!GiEs7bLsOG=Np_2Mk9eWYQq5qRc5m;4Fu4hm",
        "MLMDWLHfl%0;2DHgBBu*%WCq/R.RZ-q7mPAYwaANJKTxneOr*XDsKrt1",
        "ItEFM@GwZ=imC67yBg25CbCrCdbB7.w?tKOCW?ATDuYt_zoGncr0*-94",
        "qjC5lE*WQo=#r4bGgj1fDgua*Vled!E4Xgi9%R4/4;Zf5wl5BlUdKe1K",
        "*y=Qpi;XjMrKVoNLr1yGGPrP63c#yyt=2Y;QYogV/B_tFakjYGzf2vEw",
        "H0g9FFw7pxf;r_@MVcGET;KaP=qr/ONHTYJuHuQ=cLEe!n%bQmKaoAdJ",
        "qBqLmE7bkY0T+SFxLNzH=2k2BPbkD9pP5/DGDEeXaz?u9wn;hjObXVQ-",
        "o-KX0XXx5hY-O.uH5c3Q8@=byeCqvO!v6PoP=HuCXT-bXz5i-Hs!p/Cy",
        "?6vVU3CrlzE/hfNy50-oBoD/vr1nsAG6M@UKiY5vRU@vu6Ba4=N*Llwd",
        "2wx0Cl.ltV9qLDPh?QW4yfb*qVuz+QvxR@e9xFt3WbBZ_iV@03??P#2S",
        "R8K3#WycxLYUvd6*upiWeC%H/3.HW0iZKIo*!**A0vz7JASX_FXwgg%S",
        "%C3KXrG7r.Jn*??40is5aI..lxKRc9=1PV_%I1zJp2HK#1hCKp!.+J%7",
        "E5wxg-r#FeXr;Bw8vjQuA+H6PIG9@?@FcM-aFBz0LPFje@FJ=;Pb=V6Q",
        "x@hY54BLnSO7MW=46mj85ucUC;=%2mLHcvzPrb!dO=!LyU_uEird8.iC",
        "kIV7+IgBGHfot0MfrFt-zQx5BW-T!5VYKw1jFKqFk?Fq*eAaVL82ctaU",
        "hn6nUMGY9Bb_/0agmna5KG%tVl;6_pBpWhBpbC8T044-DEbs8cP-TsJa",
        "uMR6OWcT/8wpy7Fk2C=dZEqRJb;raNW1Xx?P7CJocl*Re9?AS2IQy3Bp",
        "tAw9Z7=8m!RA@@zz=cB-PZ+Xh_UuFGCrwcs7dWOrrD2ir?KgCfWV1NaQ",
        "LYcdnV3f%xalU%WtNgTLtb2n8+rw63*jBYtE?8DVXWq?qwMKIzIU=Ox9",
        "t*rz1HvJx#j!VSzbd;-X88_MyhnGvrp+y7iERbrQxeJzNQ-@A3#!aaB7",
        "%fBUqpnQYwUl7vKBYuwezU/itwG%0ujY6xM8aXwI@=an!ZFbs*p20uIv",
        "gpy=ivn1M_2KjZyG;nn5=imPj1W+E3uMonc7w2xRdwa/u!%TOj5NGZ1.",
        "DeZ3Z0wgZqs!;#gS*VcTHmvv8Dvr.gFMye3Rd?%0A?xEZ;WWCmY7_i@n",
        "60?VEEh%kdJqLP*!dzTM*rV-4ZpiIVSbnPiz/tv6d4r_-2L9xceTO1xP",
        "fGo1m/7PK5C!4N_6DYzI/Z/BO272@.M9pSU=F?fk_b8G0QFenC_=DiCd",
        "aLyUWm*t/je_5anUWS/cZgnP.jX4saEXrWI;?-Z?Zmrf-ynWmyt7iwE5",
        "njW9PHAvfGbjRpah=FD+-HaW_BS?3T*?SOYipn1Rl!A4XDE_YM#ctQGo",
        "IA@N*G%KOhRCR#%ca8Z%Gs/%_VsrbjYBI7ZpwobfD+n*9B2gTlB7#Ohm",
        "B;RqkX75JP?gpZ3yb?-TFJG?qMX*hnK?a9oyW8?2C53tHV#?5Pa;ZNh#",
        "#W/YA!XsFAG28Emv/IQXcj+RRdHSMMrMcw7A8t2HhKnGOLI1z1;Q_Kme",
        "i1Ct+Ff%f?aMUm+ddJ*R_4HJVdpiPbPOIcoHqJTc2uyT8!gN#r++UUi1",
        "u=Yhm;0E2IC%;39T*jmYG+XZ_cqvnb;*=+jx89@u5to0=oPL4q;84Kv7",
        "t.kJeFoaLM.KTMiDt5djVtffNf@_eLI5PzD9Y4a*FOp4pF#kG=P/3Ll/",
        "pF!.Or!/EP?rIw2*fodHDy*IoV-l_ICDNETS8bxlSq0aKXjiNZrP4d!2",
        "N+h=2hC!N#dEckDD#%*k.l;Q8cXto+V6IcB;?357o4MS?fG0WnY*oi%1",
        "*QIaKfeVkt!AiFJKKF_RZ2WdWuXAmgSxjWI0vewIZ9xXkGRQeN1jmycK",
        "?1EnIe-MaUnL;glta+#mF.%QcGtY26L7?;5=aJA!.l;PpFT;z.Dy6@F!",
        "9+KB7mgrI2R/xrcW9_orBzxSzvw33jAE%+Hm./KiEdH9-TXHt3*+Yl?J",
        "M-NM@0+w1NG+j*X+8Fmvs-hqYpJ/uN2Z3.ZK8.saphHyMju4gCCT.WnY",
        "dt+Yq-Cohz?lmNB9jMFL36e-?cqrLigtiB3OLU7raqQkoJSSAP69Qcrt",
        "P.dOVhd/TYDlg9yPbb2Np=X8JXr!yucFgK/M#sf.MRQPF48Lgrh*xeDV",
        "yrE;@hXw_;EW+mY6_3AY0H2clcfHMmfBG0kWohI2?rTIzmaATx==d2sj",
        "0yikCQjgpDQKBBSlvVeI+u0/Ce+ltmubH=h*.v01I9pvWQ/SffUIXk3Q",
        ";jhuc;+CZ-CHqcmAs0ABEE09fsFORmDuNR#mNFYK/nQhN#yD%Umuk2L=",
        "5vP6#v7%8f?Zk/9gW*Bo-Tvn=CV;/fJiF.4o@oTb2Xv4;Zp=i/Sv1uFm",
        "Ay@-Z+%dVjUmSUw9sx8nv#qkdcl/Ml4i=RoPyeB#eSilECxNagCE8o#I",
        "HVb;Y=Q729tA2gLNv=IcfFmh1Yw3jzwWWfHdCEqlBy6Sgxi=U-f#fl0C",
        "5?SAUjYCMRwWZ4Kraub3WTBE@2iuU5!*2EvI9pRhV3GOALrm0-n8K_3l",
        "=NKoB14eW7rUJC47c9dpN9D1L_=fRJ25YJPvfznw/z%QrWcpF2gXpNYG",
        "JgdE7YMMF8!A+IczV4qk?+K-qRYl0ePK8Im/Wi-xn2L5ac-.Z3B7u+MS",
        "SplpAxjxs8P1tdd+==;OybkNdaL_cen;!P03;Vx*TkMpNJb_aO#1@.u?",
        "EXMwmmULRS5d0w*uTI?OO?AT/PsNn8G;s5zwJAZ3P.U?b;S0YTi4Dg2K",
        "X%xW?HxjL88?;QlPvO.PxF%B;%-TaZ=MDoZRaD9IY86pMX3I5P/*imkR",
        "/wZBSeI9#v#f#U%2b*#-VZ-oJ3kKZE1nEHR107@=z;yngXQlXUlsuv!x",
        "70*l=.j9?aa6COBq/.MYyXQ-m.+FqI=aKr=A?bA1l-tF_hiJS?twju/k",
        "jB/?KeT8h?uiVAvu.h990L#Fm19vmT;orfo=cFqFk7uC9A-SV/yB7M6Y",
        "J=fgrvWO_F@*j2R=#2R=n*Sm@ramr_4fvP7jwJm_lfF4J.5iu5KE!@7T",
        "y%qAU6tAYiRT!5T4QUfH6ld#86598zj4fn74qly19Efx3mv/mc-xizkK",
        "fyPkB7Ow.j-e;lO2s0V1F2LWL6Wf+9W3DvR#WO0B*6zUyQBAOJE4d5H7",
        "n%ZSk68KKHbZUXfeAT*WD#v!4WR14oZUnhad-hoG;mHq-z3#0x4J=83w",
        "UdlOpGev5G89_Cpis5Yn7EHEpZBH7Pb210Ii_ao#Ii74@?!xHPeKkQ*Q",
    };
			
	private static final String ENCRYPTION = "Blowfish";
	public static final Charset CHARSET = Charset.forName("UTF-8");
	private Cipher cipher;
	
	private static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz_";
	private static final Random rnd = new Random();

	private static final String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	public static final String generateRandomFilename(String path, String prefix, int length, String suffix, FileIO io) throws IOException {
		String fileName;
		do {
			fileName = path + "/" + prefix + generateRandomString(length) + suffix;
		} while (io.exists(fileName));
		return fileName;
	}
	
	public FileUtils() {
		try {
			cipher = Cipher.getInstance(ENCRYPTION);
		} catch (NoSuchAlgorithmException e) {
			AliteLog.e("FileUtils initializer", "Encryption not available");
		} catch (NoSuchPaddingException e) {
			AliteLog.e("FileUtils initialized", "Padding not available");
		}
	}
	
	@SuppressLint("TrulyRandom")
	public final byte [] encrypt(byte [] toEncrypt, String strKey) {
		byte [] result = toEncrypt;
		if (cipher != null) {
			try {
				SecretKeySpec key = new SecretKeySpec(strKey.getBytes(CHARSET), ENCRYPTION);
				strKey = null;
				cipher.init(Cipher.ENCRYPT_MODE, key);
				result = cipher.doFinal(toEncrypt);
			} catch (Exception e) {
				AliteLog.e("Encrypt", "Error During Encryption", e);
			}
		}
		strKey = null;
		return result;
	}

	public final byte [] decrypt(byte [] toDecrypt, String strKey) {
		byte [] result = toDecrypt;
		if (cipher != null) {
			try {
				SecretKeySpec key = new SecretKeySpec(strKey.getBytes(CHARSET), ENCRYPTION);
				strKey = null;
				cipher.init(Cipher.DECRYPT_MODE, key);
				result = cipher.doFinal(toDecrypt);
			} catch (Exception e) {
				AliteLog.e("Decrypt", "Error During Decryption", e);
			}
		}
		strKey = null;
		return result;
	}

	public final byte [] encodeLongs(long [] toEncrypt, String strKey) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(toEncrypt.length * 8);
		for (long l: toEncrypt) {
			buffer.putLong(l);
		}
		byte [] input = new byte[toEncrypt.length * 8];
		buffer.flip();
		buffer.get(input);
		return encrypt(input, strKey);
	}
	
	public final long [] decodeLongs(byte [] toDecrypt, String strKey) {
		byte [] decrypted = decrypt(toDecrypt, strKey);
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(decrypted.length);
		buffer.put(decrypted);
		buffer.flip();
		long [] result = new long[buffer.limit() / 8];
		for (int i = 0; i < buffer.limit() / 8; i++) {
			result[i] = buffer.getLong(i);
		}

		return result;
	}
		
	private final void writeString(DataOutputStream dos, String string, int size) {
		int len = string.length();
		while (len < size) {
			string += " ";
			len = string.length();
		}
		if (len > size) {
			string = string.substring(0, size);
		}
		try {
			dos.write(string.getBytes(CHARSET));
		} catch (IOException e) {
			AliteLog.e("[ALITE] File Utils", "Cannot write String.", e);
		}
	}

	private final String readString(DataInputStream dis, int size) {
		byte [] input = new byte[size];
		try {
			dis.read(input);
			return new String(input, CHARSET);
		} catch (IOException e) {
			AliteLog.e("[ALITE] File Utils", "Cannot read String.", e);
		}
		return null;
	}
				
	private final byte[] zipBytes(final byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry("_");
		entry.setSize(input.length);
		zos.putNextEntry(entry);
		zos.write(input);
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();
	}
		
	private final byte[] unzipBytes(final byte[] input) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(input);			 
		byte [] buffer = new byte[1024];
		ZipInputStream zip = new ZipInputStream(bais);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
		int read = 0;			 
		if (zip.getNextEntry() != null) {
			while ((read = zip.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
		}			 
		zip.close();
		baos.close();			 
		return baos.toByteArray();
	}
		
	private final void equipLaser(int where, Laser laser, PlayerCobra cobra) {
		if ((where & 1) > 0) cobra.setLaser(PlayerCobra.DIR_FRONT, laser);
		if ((where & 2) > 0) cobra.setLaser(PlayerCobra.DIR_RIGHT, laser);
		if ((where & 4) > 0) cobra.setLaser(PlayerCobra.DIR_REAR,  laser);
		if ((where & 8) > 0) cobra.setLaser(PlayerCobra.DIR_LEFT,  laser);
	}
	
	private void setEquipped(PlayerCobra cobra, Equipment equip, boolean value) {
		if (value) {
			cobra.addEquipment(equip);
		} else {
			cobra.removeEquipment(equip);
		}
	}

	private final int readByte(DataInputStream dis) throws IOException {
		return ((int) dis.readByte()) & 0xFF;
	}
	
	public final void loadCommander(Alite alite, DataInputStream dis) throws IOException {
		Player player = alite.getPlayer();
		GalaxyGenerator generator = alite.getGenerator();
		PlayerCobra cobra = player.getCobra();
		cobra.reset();
		player.setName(readString(dis, 16).trim());
		generator.buildGalaxy(readByte(dis));
		char [] seed = new char [] {
				dis.readChar(),
				dis.readChar(),
				dis.readChar()
		};
		generator.buildGalaxy(seed[0], seed[1], seed[2]);
		player.setCurrentSystem(generator.getSystem(readByte(dis)));		
		player.setHyperspaceSystem(generator.getSystem(readByte(dis)));
		if (player.getCurrentSystem() == null) {
			if (player.getHyperspaceSystem() == null) {
				player.setHyperspaceSystem(generator.getSystem(0));
				player.setCurrentSystem(generator.getSystem(0));
			} else {
				player.setCurrentSystem(player.getHyperspaceSystem());
			}
		}
		cobra.setFuel(readByte(dis));
		player.setCash(dis.readLong());
		player.setRating(Rating.values()[readByte(dis)]);
		player.setLegalStatus(LegalStatus.values()[readByte(dis)]);
		alite.setGameTime(dis.readLong());
		player.setScore(dis.readInt());
		player.getMarket().setFluct(dis.read());
		player.getMarket().generate();
		AliteLog.d("LOADING COMMANDER", "Parsing Tradegoods");
		for (TradeGood tg: TradeGoodStore.get().goods()) {
			player.getMarket().setQuantity(tg, dis.read());
		}
		for (TradeGood tg: TradeGoodStore.get().goods()) {
			cobra.addTradeGood(tg, Weight.grams(dis.readInt()), 0);
		}
		player.setLegalValue(dis.readInt());
		cobra.setRetroRocketsUseCount(dis.readInt());
		int currentSystem = dis.readInt();
		int hyperspaceSystem = dis.readInt();
		Raxxla raxxla = new Raxxla();
		if (player.getCurrentSystem() != null && player.getCurrentSystem().getIndex() == 0 && generator.getCurrentGalaxyFromSeed() == 8 && currentSystem == 1 && player.getRating() == Rating.ELITE) {
			player.setCurrentSystem(raxxla.getSystem());
		}
		if (player.getHyperspaceSystem() != null && player.getHyperspaceSystem().getIndex() == 0 && generator.getCurrentGalaxyFromSeed() == 8 && hyperspaceSystem == 1 && player.getRating() == Rating.ELITE) {
			player.setHyperspaceSystem(raxxla.getSystem());
		}
		AliteLog.d("LOADING COMMANDER", "Getting IJC, JC, and SCs");
		player.setIntergalacticJumpCounter(dis.readInt());
		player.setJumpCounter(dis.readInt());
		int grams = dis.readInt();
		if (grams != 0) {
			cobra.addSpecialCargo("Thargoid Documents", Weight.grams(grams));
		}
		int tons = dis.readInt();
		if (tons != 0) {
			cobra.addSpecialCargo("Unhappy Refugees", Weight.tonnes(cobra.isEquipmentInstalled(EquipmentStore.largeCargoBay) ? 35 : 20));
		}
		int x = dis.readInt();
		int y = dis.readInt();
		if (x != 0 || y != 0) {
			player.setPosition(x, y);
			player.setCurrentSystem(null);
		}
		dis.readLong(); // Placeholder for "special cargo"
		dis.readLong(); // Placeholder for "special cargo"
		dis.readLong(); // Placeholder for "special cargo"
		AliteLog.d("LOADING COMMANDER", "Parsing Equipment");
		int missileEnergy = readByte(dis);
		cobra.setMissiles(missileEnergy & 7);
		if (missileEnergy > 7) {
			cobra.addEquipment(EquipmentStore.extraEnergyUnit);
		}
		int equipment = readByte(dis);
		setEquipped(cobra, EquipmentStore.largeCargoBay, (equipment & 1) > 0);
		setEquipped(cobra, EquipmentStore.ecmSystem, (equipment & 2) > 0);
		setEquipped(cobra, EquipmentStore.fuelScoop, (equipment & 4) > 0);
		setEquipped(cobra, EquipmentStore.escapeCapsule, (equipment & 8) > 0);
		setEquipped(cobra, EquipmentStore.energyBomb, (equipment & 16) > 0);
		setEquipped(cobra, EquipmentStore.dockingComputer, (equipment & 32) > 0);
		setEquipped(cobra, EquipmentStore.galacticHyperdrive, (equipment & 64) > 0);
		alite.setIntergalActive((equipment & 64) > 0);
		setEquipped(cobra, EquipmentStore.retroRockets, (equipment & 128) > 0);
		int laser = readByte(dis);
		equipLaser(laser & 15, EquipmentStore.pulseLaser, cobra);
		equipLaser(laser >> 4, EquipmentStore.beamLaser, cobra);
		laser = readByte(dis);
		equipLaser(laser & 15, EquipmentStore.miningLaser, cobra);
		equipLaser(laser >> 4, EquipmentStore.militaryLaser, cobra);
		equipment = readByte(dis);
		setEquipped(cobra, EquipmentStore.navalEnergyUnit, (equipment & 1) > 0);
		setEquipped(cobra, EquipmentStore.cloakingDevice, (equipment & 2) > 0);
		setEquipped(cobra, EquipmentStore.ecmJammer, (equipment & 4) > 0);
		player.setCheater(readByte(dis) != 0);
		readByte(dis);
		readByte(dis);
		dis.readInt();		
		readByte(dis);
		dis.readShort();
		player.setKillCount(dis.readInt());

		// Deprecated: Used to contain the statistics filename here...
		byte [] buffer = new byte[23];
		dis.read(buffer, 0, 23);

		player.clearMissions();
		AliteLog.d("LOADING COMMANDER", "Loading Missions");
		try {
			int activeMissionCount = dis.readInt();
			AliteLog.d("Loading Commander", "Active missions: " + activeMissionCount);
			int completedMissionCount = dis.readInt();
			Set <Integer> missionIds = new HashSet<Integer>();
			for (int i = 0; i < activeMissionCount; i++) {
				int missionId = dis.readInt();
				Mission m = MissionManager.getInstance().get(missionId);
				if (m == null) {
					AliteLog.e("[ALITE] loadCommander", "Invalid active mission id: " + missionId + " - skipping. The commander file seems to be broken.");
					continue;
				}
				m.load(dis);
				if (!missionIds.contains(missionId)) {
					missionIds.add(missionId);
					player.addActiveMission(m);
					AliteLog.d("Loading Commander", "  Active mission: " + m.getClass().getName());
				} else {
					AliteLog.d("Warning: Duplicate mission", "  Duplicate mission: " + m.getClass().getName() + " -- ignoring.");
				}								
			}
			for (int i = 0; i < completedMissionCount; i++) {
				int missionId = dis.readInt();
				Mission m = MissionManager.getInstance().get(missionId);
				if (m == null) {
					AliteLog.e("[ALITE] loadCommander", "Invalid completed mission id: " + missionId + " - skipping. The commander file seems to be broken.");
					continue;
				}
				if (missionIds.contains(missionId)) {
					player.removeActiveMission(m);
				}
				player.addCompletedMission(m);
			}
		} catch (IOException e) {
			AliteLog.e("[ALITE] loadCommander", "Old version. Cmdr data lacks mission data", e);
			// Alite commander file version 1: Did not store mission data. Ignore...
		}
		try {
			InventoryItem [] inventory = cobra.getInventory();
			for (int i = 0; i < 18; i++) {
				long price = dis.readLong();
				cobra.setTradeGood(TradeGoodStore.get().fromNumber(i), inventory[i].getWeight(), price);
			}
		} catch (IOException e) {
			AliteLog.e("[ALITE] loadCommander", "Old version. Cmdr data lacks price data for inventory", e);
		}
		try {
			for (int i = 0; i < 18; i++) {
				long weightInGrams = dis.readLong();
				cobra.setUnpunishedTradeGood(TradeGoodStore.get().fromNumber(i), Weight.grams(weightInGrams));
			}
		} catch (IOException e) {
			AliteLog.e("[ALITE] loadCommander", "Old version. Cmdr data lacks unpunished data for inventory", e);
		}

		AliteLog.d("[ALITE] loadCommander", String.format("Loaded Commander '%s', galaxyNumber: %d, seed: %04x %04x %04x", player.getName(), generator.getCurrentGalaxy(), (int) generator.getCurrentSeed()[0], (int) generator.getCurrentSeed()[1], (int) generator.getCurrentSeed()[2]));		
	}
	
	public final void loadCommander(Alite alite, String fileName) throws IOException {
		AliteLog.d("LOADING COMMANDER", "Filename = " + fileName);
		byte [] commanderData = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(alite.getFileIO().readPartialFileContents(fileName, 2));
		int length = bis.read() * 256 + bis.read();
		commanderData = unzipBytes(decrypt(alite.getFileIO().readFileContents(fileName, 2 + length), 
				getKey(fileName)));
		if (commanderData == null) {
			throw new IOException("Ouch! Couldn't load commander " + fileName + ". No changes to current commander were made.");
		}
		bis = new ByteArrayInputStream(commanderData);
		DataInputStream dis = new DataInputStream(bis);	
		loadCommander(alite, dis);
		dis.close();
		AliteLog.d("LOADING COMMANDER", "DONE...");
	}
		
	public final void saveState(FileIO fileIO, Screen screen) throws IOException {
		AliteLog.d("Saving state", "Saving state. Screen = " + (screen == null ? "<null>" : screen.getClass().getName()));		
		int screenCode = screen == null ? -1 : screen.getScreenCode();
		if (screenCode != -1) {
			OutputStream stateFile = fileIO.writeFile(AliteStartManager.ALITE_STATE_FILE);
			stateFile.write(screenCode);
			screen.saveScreenState(new DataOutputStream(stateFile));
			stateFile.close();
			AliteLog.d("Saving state", "Saving state completed successfully.");
		} else {
			fileIO.deleteFile(AliteStartManager.ALITE_STATE_FILE);
			AliteLog.d("Saving state", "Saving state could not identify current screen, hence the state file was deleted.");
		}				
	}
	
	public final void saveState(FileIO fileIO, byte screenCode, byte [] data) throws IOException {
		AliteLog.d("Saving state", "Saving state. Screen = " + screenCode);
		OutputStream stateFile = fileIO.writeFile(AliteStartManager.ALITE_STATE_FILE);
		stateFile.write(screenCode);
		stateFile.write(data);
		stateFile.close();
		AliteLog.d("Saving state", "Saving state completed successfully.");
	}
	
	public final boolean readState(Alite alite, FileIO fileIO) throws IOException {		
		byte [] state = fileIO.readFileContents(AliteStartManager.ALITE_STATE_FILE);
		AliteLog.d("Reading state", "State == " + (state == null ? "" : ", " + (int) state[0]));
		if (state != null && state.length > 0) {
			return ScreenBuilder.createScreen(alite, state);
		}
		return false;
	}
		
	public final void saveCommander(final Alite alite, DataOutputStream dos) throws IOException {
		Player player = alite.getPlayer();
		GalaxyGenerator generator = alite.getGenerator();
		PlayerCobra cobra = player.getCobra();
		int marketFluct = player.getMarket().getFluct();
		List <Integer> quantities = player.getMarket().getQuantities();
		writeString(dos, player.getName(), 16);
		dos.writeByte(generator.getCurrentGalaxy());
		char [] seed = generator.getCurrentSeed();
		dos.writeChar(seed[0]);
		dos.writeChar(seed[1]);
		dos.writeChar(seed[2]);
		int currentSystem = player.getCurrentSystem() == null ? 0 : player.getCurrentSystem().getIndex();
		int hyperspaceSystem = player.getHyperspaceSystem() == null ? 0 : player.getHyperspaceSystem().getIndex();
		dos.writeByte(currentSystem == 256 ? 0 : currentSystem);
		dos.writeByte(hyperspaceSystem == 256 ? 0 : hyperspaceSystem);
		dos.writeByte(cobra.getFuel());
		dos.writeLong(player.getCash());
		dos.writeByte(player.getRating().ordinal());
		dos.writeByte(player.getLegalStatus().ordinal());
		dos.writeLong(alite.getGameTime());
		dos.writeInt(player.getScore());
		dos.write(marketFluct);
		for (int quantity: quantities) {
			dos.write(quantity);
		}
		for (InventoryItem w: cobra.getInventory()) {
			dos.writeInt((int) w.getWeight().getWeightInGrams());
		}
		dos.writeInt(player.getLegalValue());
		dos.writeInt(cobra.getRetroRocketsUseCount());
		dos.writeInt(currentSystem == 256 ? 1 : 0);
		dos.writeInt(hyperspaceSystem == 256 ? 1 : 0);
		dos.writeInt(player.getIntergalacticJumpCounter());
		dos.writeInt(player.getJumpCounter());
		Weight w = cobra.getSpecialCargo("Thargoid Documents");
		dos.writeInt(w == null ? 0 : (int) w.getWeightInGrams());
		w = cobra.getSpecialCargo("Unhappy Refugees");
		dos.writeInt(w == null ? 0 : (int) w.getWeightInGrams());	
		if (player.getCurrentSystem() == null && player.getPosition() != null) {
			dos.writeInt(player.getPosition().x);
			dos.writeInt(player.getPosition().y);
		} else {
			dos.writeLong(0);			
		}
		dos.writeLong(0); // Placeholder for "special cargo"
		dos.writeLong(0); // Placeholder for "special cargo"
		dos.writeLong(0); // Placeholder for "special cargo"
		dos.writeByte(cobra.getMissiles() + (cobra.isEquipmentInstalled(EquipmentStore.extraEnergyUnit) ? 8 : 0));
		int equipment = (cobra.isEquipmentInstalled(EquipmentStore.largeCargoBay)      ?   1 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.ecmSystem)          ?   2 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.fuelScoop)          ?   4 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.escapeCapsule)      ?   8 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.energyBomb)         ?  16 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.dockingComputer)    ?  32 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.galacticHyperdrive) ?  64 : 0) +
						(cobra.isEquipmentInstalled(EquipmentStore.retroRockets)       ? 128 : 0);
		dos.writeByte(equipment);
		int [] lasers = new int [] {0, 0, 0, 0};
		Laser laser;
		if ((laser = cobra.getLaser(PlayerCobra.DIR_FRONT)) != null) {lasers[laser.getIndex()]++;}
		if ((laser = cobra.getLaser(PlayerCobra.DIR_RIGHT)) != null) {lasers[laser.getIndex()] += 2;}
		if ((laser = cobra.getLaser(PlayerCobra.DIR_REAR))  != null) {lasers[laser.getIndex()] += 4;}
		if ((laser = cobra.getLaser(PlayerCobra.DIR_LEFT))  != null) {lasers[laser.getIndex()] += 8;}
		dos.writeByte(lasers[0] + (lasers[1] << 4));
		dos.writeByte(lasers[2] + (lasers[3] << 4));
		equipment = (cobra.isEquipmentInstalled(EquipmentStore.navalEnergyUnit) ? 1 : 0) +
				    (cobra.isEquipmentInstalled(EquipmentStore.cloakingDevice) ? 2 : 0) +
				    (cobra.isEquipmentInstalled(EquipmentStore.ecmJammer) ? 4 : 0);
		dos.writeByte(equipment);
		dos.writeByte(player.isCheater() ? 1 : 0);
		dos.writeByte(0); // Placeholder for "special equipment"
		dos.writeByte(0); // Placeholder for "special equipment"
		dos.writeInt(0); // Placeholder for "special equipment"
		dos.writeByte(0); // Placeholder for number of kills to next "Right On, Commander"-Msg.
		dos.writeShort(0); // Placeholder for number of kills to next "Good Shooting, Commander"-Msg.
		dos.writeInt(player.getKillCount());		
		// Dummy String: Deprecated statistics filename
		writeString(dos, "12345678901234567890123", 23);		
		dos.writeInt(player.getActiveMissions().size());
		dos.writeInt(player.getCompletedMissions().size());
		for (Mission m: player.getActiveMissions()) {
			dos.writeInt(m.getId());
			dos.write(m.save());
		}
		for (Mission m: player.getCompletedMissions()) {
			dos.writeInt(m.getId());
		}
		for (InventoryItem item: cobra.getInventory()) {
			dos.writeLong(item.getPrice());
		}
		for (InventoryItem item: cobra.getInventory()) {
			dos.writeLong(item.getUnpunished().getWeightInGrams());
		}
	}
	
	private String determineOldestAutosaveSlot(Alite alite) throws IOException {
		String autosaveFilename = "commanders/__autosave";		
		long oldestDate = 0;
		String oldestFilename = "";
		for (int i = 0; i < 3; i++) {
			String fileName = autosaveFilename;
			if (i != 0) {
				fileName += i;
			}
			fileName += ".cmdr";
			long date = alite.getFileIO().fileLastModifiedDate(fileName);
			if (date == 0) {
				return fileName;
			}
			// "date" returns the time passed since 1970.
			// Hence, a smaller value means that the file is older, because it has been saved earlier. 
			if (date < oldestDate || oldestDate == 0) {
				oldestDate = date;
				oldestFilename = fileName;
			}
		}
		return oldestFilename;
	}
	
	private String determineYoungestAutosaveSlot(Alite alite) throws IOException {
		String autosaveFilename = "commanders/__autosave";		
		long youngestDate = 0;
		String youngestFilename = autosaveFilename + ".cmdr";
		for (int i = 0; i < 3; i++) {
			String fileName = autosaveFilename;
			if (i != 0) {
				fileName += i;
			}
			fileName += ".cmdr";
			long date = alite.getFileIO().fileLastModifiedDate(fileName);
			if (date == 0) {
				continue;
			}
			// "date" returns the time passed since 1970.
			// Hence, a larger value means that the file is younger, because it has been saved later. 
			if (date > youngestDate) {
				youngestDate = date;
				youngestFilename = fileName;
			}
		}
		return youngestFilename;
	}

	private void copyCommander(Alite alite, String oldFileName, String newFileName, CommanderData info) throws IOException {
		byte [] commanderData = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(alite.getFileIO().readPartialFileContents(oldFileName, 2));
		int length = bis.read() * 256 + bis.read();
		commanderData = unzipBytes(decrypt(alite.getFileIO().readFileContents(oldFileName, 2 + length), 
				getKey(oldFileName)));
		if (commanderData == null) {
			throw new IOException("Ouch! Couldn't load commander " + oldFileName + ". No changes to current commander were made.");
		}
		bis = new ByteArrayInputStream(commanderData);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = bis.read(buffer)) != -1) {
		    bos.write(buffer, 0, len);
		}
		bis.close();
		commanderData = encrypt(zipBytes(bos.toByteArray()), getKey(newFileName));
		
		bos = new ByteArrayOutputStream(1024);
		DataOutputStream dos = new DataOutputStream(bos);
		writeString(dos, info.getName(), 16);
		writeString(dos, info.getDockedSystem(), 8);
		dos.writeLong(info.getGameTime());
		dos.writeInt(info.getPoints());
		bos.write(info.getRating().ordinal());	
		byte [] headerData = encrypt(bos.toByteArray(), getKey(newFileName));
		
		OutputStream fos = null;
		try {
			if (!alite.getFileIO().exists("commanders")) {
				alite.getFileIO().mkDir("commanders");
			}
			fos = alite.getFileIO().writeFile(newFileName);
			fos.write(headerData.length >> 8);
			fos.write(headerData.length & 255);
			fos.write(headerData);
			fos.write(commanderData);			
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				bos.close();
			} catch (IOException e) {
				AliteLog.e("[Alite] saveCommander", "Error when writing commander.", e);
			}
		}
		AliteLog.d("[Alite] copyCommander", "Copied Commander '" + info.getName() + "'.");
	}
	
	public final void autoSave(Alite alite) throws IOException {
		String commanderName = determineOldestAutosaveSlot(alite);
		if (alite.getFileIO().exists(commanderName)) {
			try {
				CommanderData commanderData = getQuickCommanderInfo(alite, commanderName);
				if (commanderData != null && commanderData.getGameTime() > alite.getGameTime()) {
					// Trying to overwrite an "older" commander. Backup first.
					copyCommander(alite, commanderName, FileUtils.generateRandomFilename("commanders", "", 12, ".cmdr", alite.getFileIO()), commanderData);
				}
			} catch (Exception e) {
				AliteLog.e("Error Occurred", "Error while creating backup.", e);
			}
		}
		saveCommander(alite, null, commanderName);
	}
	
	public final void autoLoad(Alite alite) throws IOException {
		String autosaveFilename = determineYoungestAutosaveSlot(alite);
		if (alite.getFileIO().exists(autosaveFilename)) {
			loadCommander(alite, autosaveFilename);
		}		
	}
		
	public final void saveCommander(Alite alite, String newName, String fileName) throws IOException {
		Player player = alite.getPlayer();
		if (newName == null) {
			newName = player.getName();
		} else {
			player.setName(newName);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		DataOutputStream dos = new DataOutputStream(bos);
		saveCommander(alite, dos);
		dos.close();
		byte [] commanderData = encrypt(zipBytes(bos.toByteArray()), getKey(fileName));
			
		bos = new ByteArrayOutputStream(1024);
		dos = new DataOutputStream(bos);
		writeString(dos, newName, 16);
		writeString(dos, player.getCurrentSystem() == null ? "Unknown" : player.getCurrentSystem().getName(), 8);
		dos.writeLong(alite.getGameTime());
		dos.writeInt(player.getScore());
		bos.write(player.getRating().ordinal());	
		byte [] headerData = encrypt(bos.toByteArray(), getKey(fileName));
		
		OutputStream fos = null;
		try {
			if (!alite.getFileIO().exists("commanders")) {
				alite.getFileIO().mkDir("commanders");
			}
			fos = alite.getFileIO().writeFile(fileName);
			fos.write(headerData.length >> 8);
			fos.write(headerData.length & 255);
			fos.write(headerData);
			fos.write(commanderData);			
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				bos.close();
			} catch (IOException e) {
				AliteLog.e("[Alite] saveCommander", "Error when writing commander.", e);
			}
		}
		AliteLog.d("[Alite] saveCommander", "Saved Commander '" + player.getName() + "'.");
	}
	
	public CommanderData getQuickCommanderInfo(Alite alite, String fileName) {
		byte [] commanderData = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(alite.getFileIO().readPartialFileContents(fileName, 2));
			int length = bis.read() * 256 + bis.read();
			commanderData = decrypt(alite.getFileIO().readPartialFileContents(fileName, 2, length), getKey(fileName));
			bis.close();
		} catch (IOException e) {
			AliteLog.e("[ALITE] loadCommander", "Error when loading commander " + fileName + ".", e);
			return null;
		} 			
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(commanderData);
			DataInputStream dis = new DataInputStream(bis);
			String name = readString(dis, 16).trim();
			String currentSystem = readString(dis, 8).trim();
			long gameTime = dis.readLong();
			int points = dis.readInt();
			Rating rating = Rating.values()[bis.read()];	
			bis.close();
			return new CommanderData(name, currentSystem, gameTime, points, rating, fileName);
		} catch (IOException e) {
			AliteLog.e("[ALITE] loadCommander", "Error when loading commander "
					+ fileName + ".", e);
		}
		return null;
	}
	
	public final static String computeSHAString(File f) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			InputStream is = new FileInputStream(f);
			DigestInputStream dis = new DigestInputStream(is, md);
			byte [] buffer = new byte[1024 * 1024];
			while (dis.read(buffer) != -1) 
				;
			dis.close();
	        byte[] mdbytes = md.digest();
	   	 
	        StringBuffer hexString = new StringBuffer();
	    	for (int i = 0; i < mdbytes.length; i++) {
	    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
	    	}				
	    	return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			AliteLog.e("[ALITE] computeSHAString", "No SHA-256 encryption!", e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ""; 		
	}
	
	public final String computeSHAString(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
	        md.update(text.getBytes(CHARSET));
	        byte[] mdbytes = md.digest();
	 
	        StringBuffer hexString = new StringBuffer();
	    	for (int i = 0; i < mdbytes.length; i++) {
	    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
	    	}				
	    	return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			AliteLog.e("[ALITE] computeSHAString", "No SHA-256 encryption!", e);
		}
		return ""; 
	}
	
	private String getKey(String fileName) {
		int keyIndex = fileName.hashCode();		
		if (keyIndex == Integer.MIN_VALUE) {
			keyIndex = Integer.MAX_VALUE;
		} else {
			keyIndex = Math.abs(keyIndex);
		}
		keyIndex %= keys.length;
		return keys[keyIndex];		
	}
}
