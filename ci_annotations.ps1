$out = 'E:\GitHub\TpOffline\ci-check-output.txt'
$runId = $args[0]
$jobsJson = (curl.exe -s --connect-timeout 20 "https://api.github.com/repos/ALingqing/TpOffline/actions/runs/$runId/jobs") | Out-String
$j = $jobsJson | ConvertFrom-Json
$job = $j.jobs | Select-Object -First 1
$checkRunUrl = $job.check_run_url
Add-Content -Path $out -Value "=== annotations for run $runId ==="
$ann = (curl.exe -s --connect-timeout 20 "$checkRunUrl/annotations") | Out-String
$a = $ann | ConvertFrom-Json
if ($a -is [array]) {
    $a | ForEach-Object { Add-Content -Path $out -Value "[$($_.annotation_level)] $($_.path):$($_.start_line) $($_.message)" }
} else {
    Add-Content -Path $out -Value "annotations raw: $ann"
}
