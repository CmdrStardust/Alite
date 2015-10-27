Alite v. 1.2 by Philipp Bouillon and Duane McDonnell

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
specified in the AliteStartManager (currently 2180).

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
