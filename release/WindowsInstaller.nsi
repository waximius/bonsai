; example2.nsi
;
; This script is based on example1.nsi, but it remember the directory, 
; has uninstall support and (optionally) installs start menu shortcuts.
;
; It will install example2.nsi into a directory that the user selects,

;--------------------------------

; The name of the installer
Name "Bonsai"

; The file to write
OutFile "BonsaiInstaller.exe"

; The default installation directory
InstallDir $PROGRAMFILES\Bonsai

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Bonsai" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin

;--------------------------------
; Pages

Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; Auto un-install if already on system
Function .onInit
 
  ReadRegStr $R0 HKLM \
  "Software\Microsoft\Windows\CurrentVersion\Uninstall\Bonsai" \
  "UninstallString"
  StrCmp $R0 "" done
 
  MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION \
  "Bonsai is already installed. $\n$\nClick 'OK' to remove the \
  previous version or 'Cancel' to cancel this upgrade." \
  IDOK uninst
  Abort
 
;Run the uninstaller
uninst:
  ClearErrors
  Exec $INSTDIR\uninstall.exe
 
done:
 
FunctionEnd

;--------------------------------

; The stuff to install
Section "Bonsai"

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File /r "Bonsai\*"
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\Bonsai "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Bonsai" "DisplayName" "Bonsai"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Bonsai" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Bonsai" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Bonsai" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
  ; Fix a registry error in Java
  ;ReadRegStr $R0 HKLM "Software\JavaSoft\Prefs" ""
  ;StrCmp $R0 "" 0 no_javaprefs
  ;  WriteRegStr HKLM "Software\JavaSoft\Prefs" "" ""
  ;no_javaprefs:
  
  ; Register file type associations
  ;WriteRegStr HKCR ".tree" "" "Bonsai.tree"
  ;WriteRegStr HKCR "Bonsai.atf" "" "Bonsai Tree File"
  ;WriteRegStr HKCR "Bonsai.atf\DefaultIcon" "" "$INSTDIR\Bonsai.exe,0"
  ;ReadRegStr $R0 HKCR "Bonsai.atf\shell\open\command" ""
  ;StrCmp $R0 "" 0 no_atfopen
  ;  WriteRegStr HKCR "Bonsai.atf\shell" "" "open"
  ;  WriteRegStr HKCR "Bonsai.atf\shell\open\command" "" '$INSTDIR\Bonsai.exe "%1"'
  ;no_atfopen:
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Bonsai"
  CreateShortCut "$SMPROGRAMS\Bonsai\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\Bonsai\Bonsai.lnk" "$INSTDIR\Bonsai.exe" "" "$INSTDIR\Bonsai.exe" 0
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Desktop Shortcut"

  CreateShortCut "$DESKTOP\Bonsai.lnk" "$INSTDIR\Bonsai.exe" "" "$INSTDIR\Bonsai.exe" 0
  
SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Bonsai"
  DeleteRegKey HKLM SOFTWARE\Bonsai

  ; Remove files and uninstaller
  RMDir /r $INSTDIR

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\Bonsai\*.*"
  Delete "$DESKTOP\Bonsai.lnk"

  ; Remove directories used
  RMDir "$SMPROGRAMS\Bonsai"
  RMDir "$INSTDIR"
  
  ; Remove file association
  ;ReadRegStr $R0 HKCR ".atf" ""
  ;StrCmp $R0 "Bonsai.atf" 0 +2
  ;  DeleteRegKey HKCR ".atf"
  ;DeleteRegKey HKCR "Bonsai.atf"

SectionEnd
