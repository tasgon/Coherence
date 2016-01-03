
.\buildScript.ps1

$name = Get-ChildItem -Name .\build\libs

Write-Output "Setting up client for testing"
$mc = $env:APPDATA + "\.minecraft"

Copy-Item -Verbose $mc\mods\JourneyMap* .

Remove-Item -Verbose -Recurse $mc\mods
Remove-Item -Verbose -Recurse $mc\config
Remove-Item -Verbose -Recurse $mc\coherence*
Remove-Item -Verbose -Recurse $mc\old*
New-Item -Verbose -ItemType directory $mc\mods
Copy-Item -Verbose .\build\libs\$name $mc\mods\
Move-Item -Verbose .\JourneyMap* $mc\mods\

Write-Output "Setting up server for testing"
$server = $env:HOMEPATH + "\Desktop\1.7 Test Server"

Remove-Item -Verbose $server\mods\coherence*
Copy-Item .\build\libs\$name $server\mods