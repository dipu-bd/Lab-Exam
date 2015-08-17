; Java App Launcher
;---------------------

; You want to change the below lines   
Name "Lab Exam Server"   
Caption "Lab Exam Server"    
Icon "server.ico"    
OutFile "LabExamServer.exe"

; param below can be user, admin    
RequestExecutionLevel user

SilentInstall silent
AutoCloseWindow true
ShowInstDetails show

Section ""    
  ; command to execute    
  StrCpy $0 'javaw -jar LabExamServer.jar'  
  SetOutPath $EXEDIR    
  Exec $0    
SectionEnd