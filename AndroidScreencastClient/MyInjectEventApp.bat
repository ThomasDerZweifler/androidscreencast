set PATH=%PATH%;C:\Users\a015843\Documents\perso\android-sdk-windows-1.5_r3_\platforms\android-1.5\tools
cmd /c dx --dex --output=C:\Users\a015843\eclipse-workspace\AndroidScreencastClient\classes.dex C:\Users\a015843\eclipse-workspace\AndroidScreencastClient\MyInjectEventApp.jar
aapt add MyInjectEventApp.jar classes.dex
adb push MyInjectEventApp.jar /data/
del classes.dex
del MyInjectEventApp.jar