; Java App Launcher
;---------------------

; You want to change the below lines   
Name "Lab Exam Client"   
Caption "Lab Exam Client"    
Icon "client.ico"    
OutFile "LabExamClient.exe"

; param below can be user, admin    
RequestExecutionLevel admin

SilentInstall silent
AutoCloseWindow true
ShowInstDetails show

Section ""    
  ; command to execute    
  StrCpy $0 'javaw -jar LabExamClient.jar'  
  SetOutPath $EXEDIR    
  Exec $0    
SectionEnd