$jobId = $args[0]
$out = "$env:TEMP\joblog-$jobId.txt"
curl.exe -sL --connect-timeout 30 -H "Accept: application/vnd.github+json" "https://api.github.com/repos/ALingqing/TpOffline/actions/jobs/$jobId/logs" -o $out
$size = (Get-Item $out).Length
"log size: $size"
# 提取关键错误行
$lines = Get-Content $out -Encoding UTF8
"=== 错误相关行 ==="
$lines | Select-String -Pattern 'FAILURE|What went wrong|Could not|error:|Exception|Caused by' | Select-Object -First 30 | ForEach-Object { $_.Line }
