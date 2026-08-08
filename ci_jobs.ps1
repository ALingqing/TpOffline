$out = 'E:\GitHub\TpOffline\ci-check-output.txt'
$runId = $args[0]
$jobs = (curl.exe -s --connect-timeout 20 "https://api.github.com/repos/ALingqing/TpOffline/actions/runs/$runId/jobs") | Out-String
Set-Content -Path $out -Value $jobs
$j = $jobs | ConvertFrom-Json
Set-Content -Path $out -Value "=== jobs for run $runId ==="
foreach ($job in $j.jobs) {
    Add-Content -Path $out -Value "job: $($job.name) conclusion=$($job.conclusion) id=$($job.id)"
    foreach ($step in $job.steps) {
        Add-Content -Path $out -Value "  step: $($step.name) conclusion=$($step.conclusion)"
    }
}
