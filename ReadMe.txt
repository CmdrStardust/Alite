Alite v. 1.4.1 by Philipp Bouillon and Duane McDonnell

About
-----

Alite is a fan port of the 1980s game Elite, made for Android devices. For
more information, visit http://alite.mobi or download the release from the
PlayStore:

https://play.google.com/store/apps/details?id=de.phbouillon.android.games.alite

Command your Cobra space ship in a fantastic voyage of discovery, adventure,
and trade, a supreme test of your combat, navigational and entrepreneurial
skills.

Designed specifically for Android devices, Alite brings space action and
exploration directly to you - wherever you are.

Version History
---------------

Version 1.4.1 Bugfixes: Corrupt image replaced, Planet Info in WitchSpace
	      enabled, Asteroid course randomized, tutorials now work if the
	      legal status is not clean. Medium Speed Docking Computer added.
	      [01/23/2016]

Version 1.4.0 Introduces the TimeWarp feature, implemented by Steven Phillips.
              The TimeWarp lets you pass those regions in space where the torus
              drive is not an option. Furthermore, 1.4.0 comes with the ability
              to eject cargo, and does not punish the player for scooping up
              contraband goods anymore. You can now also alter the sound volume
              for different sets of effects. Some bugfixes were made and a
              (configurable) vibration effect was added. [01/10/2016]

Version 1.3.4 Fixes a bug in the Galaxy and Local Screens. The background was
              not cleared correctly on some devices. [12/21/2015]

Version 1.3.3 Fixes a bug in the "Trading" and "Basic Flying" tutorials, which
              was introduced in version 1.3.0. [12/15/2015]

Version 1.3.2 Fixes a bug in the Equipment screen: If you had turned off
              animations (via Options/Display Options), the game would crash.
              [12/12/2015]

Version 1.3.1 Introduces the display of expected gain (or loss) from a trade and
              the "flat button layout" (detailed here:
              http://alite.mobi/node/30). For screens larger than 1920x1080,
              library and disk screens now work, too. You can set the laser
              firing to single shot and can still change speed while firing.
              Some sound effects have been fixed (most notably, the torus drive
              sound stops playing when entering hyperspace). [12/06/2015]

Version 1.3.0 Lets you invert the controls and fixes a small overlay bug in the
              Cursor keys. [12/01/2015]

Version 1.2.1 Turns the cheat mode back off :). Ooops... I left it in
              acidentally in version 1.2.0. [10/28/2015]

Version 1.2.0 Finally fixes the vanishing objects bug and works on Nexus 9/10
              devices. [10/25/2015]

Version 1.1.2 Boomslang rename was buggy. Now the Bushmaster is really called
              "Harken". First Source code release. [09/12/2015]

Version 1.1.1 The application doesn't crash anymore when its state is restored
              (bug introduced with version 1.1). Lasers now correctly vanish
              when they hit an object. Button images in the ShipIntroScreen are
              scaled again (another bug introduced with version 1.1).
              [09/06/2015]

Version 1.1 Comes with Chris Huelsbeck's rendition of the Blue Danube. Two ships
            have been renamed to "Harken" and "Lancehead", respectively.
            [08/30/2015]

Version 1.0 First release of Alite. [08/05/2015]


Build instructions
------------------

Alite has been developed with ADT, I have never tried to set it up in Android
Studio, but it should not be too hard.

If you import Alite into ADT and connect your device, you can install it
without the need to download any additional libraries. However, there are a
couple of things you need to know:

In the class "de.phbouillon.android.games.alite.AliteStartManager", you find
the constant HAS_EXTENSION_APK, which is set to true. This means that all
assets of Alite are stored in an "obb" file, which needs to be installed in
the directory "<phone>/Android/obb/de.phbouillon.android.games.alite/main.2180.de.phbouillon.android.games.alite.obb".

Now you have two options:

--- Option 1: Use an extension file ----
(Pro: Fast testing of code changes in Alite, deployment is fast,
 Con: Devilishly complicated to create an obb file, need to create
      a new obb file for each change in assets (textures/music/...))

You can either create this file (see below) or use the existing obb file
from the PlayStore version of Alite; if you downloaded Alite from the
PlayStore, the obb file will already be on your phone. In that case, copy
it to your computer, so that you have a backup.

Note that if you deploy Alite from your Computer, it has to uninstall the
official Alite version on your phone, because it uses a different key store,
so the obb file on your phone _will be deleted_ when you first install
Alite from your computer. You will have to copy the obb file back from
your computer afterwards.

If you choose to create the obb file yourself, please download the jobb tool
from this location:
https://www.dropbox.com/sh/8xk89ekj1138waq/AACit-2wkFZIz55ZFcO6XrtIa
Copy _both files_ (jobb.jar and fat32lib.jar) to a directory on your drive.
Then copy the Resources folder from Alite to that directory as well. Open
a command prompt or shell in that directory and type:

java -jar jobb.jar -d Resources -o main.<VERSION>.de.phbouillon.android.games.alite.obb -pn de.phbouillon.android.games.alite -pv <VERSION>

and make sure that <VERSION> _exactly!_ matches the version number you
specified in the AliteStartManager, see EXTENSION_FILE_VERSION.

Once built set EXTENSION_FILE_LENGTH in AliteStartManager to the size of your
new obb file in bytes and rebuild & install Alite.

Why is that so complicated?! I wish I knew :). The thing is that the jobb tool
is pretty buggy and if you really must have an obb file, this version of the
jobb tool seems to be the one which makes most devices happy. Still: Some
devices absolutely cannot read the obb file, and for those, there is the
"all-in-one-solution".

If you want more information about extension files, I suggest you read this
article here: http://developer.android.com/google/play/expansion-files.html
(but be wary: It's a slippery road :))

--- Option 2: Do not use an extension file ----
(Pro: Easy to set-up,
 Con: Slow deployment. It takes several minutes to deploy a new Alite version;
      if you're making many small changes to the code, that can be annoying)

If you only want to enhance Alite and test it on your device, or you don't
want to bother handling OBBs, set the "HAS_EXTENSION_APK" flag to false in
the AliteStartManager class.
This tells Alite that all files have to be loaded from the assets directory.
So, if you set the flag to "false", copy all files from the Resources/assets
directory to the assets/directory.
Next, copy the files from the Resources/intro directory to the res/raw
directory ("raw" has to be created).
Now, one more thing to do:
In "AliteIntro", you'll find the method "determineIntroId(int quality)" and
several commented lines below it: uncomment those and comment the final return.
In line 179, replace the "introId = -1;" with
"introId = R.raw.alite_intro_b1920;".

Done. When you now deploy Alite, you don't have to keep an OBB file on your
phone and you'll instantly see changes to resources you made. But deployment
will take longer...

I hope this allows you to deploy Alite from your computer... If you have any
questions, please post them at the forum over at http://alite.mobi/forum.

Good luck -- and have fun!
Cmdr Stardust aka Philipp Bouillon
