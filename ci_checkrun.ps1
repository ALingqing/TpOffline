$out = 'E:\GitHub\TpOffline\ci-check-output.txt'
$runId = $args[0]
Add-Content -Path $out -Value "=== check-run output for run $runId ==="
$jobsJson = (curl.exe -s --connect-timeout 20 "https://api.github.com/repos/ALingqing/TpOffline/actions/runs/$runId/jobs") | Out-String
$j = $jobsJson | ConvertFrom-Json
foreach ($job in $j.jobs) {
    $cr = (curl.exe -s --connect-timeout 20 $job.check_run_url) | Out-String | ConvertFrom-Json
    Add-Content -Path $out -Value "job: $($job.name)"
    Add-Content -Path $out -Value "  output.title: $($cr.output.title)"
    Add-Content -Path $out -Value "  output.summary: $($cr.output.summary)"
    if ($cr.output.text) { Add-Content -Path $out -Value "  output.text: $($cr.output.text)" }
}
