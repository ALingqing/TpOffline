$out = 'E:\GitHub\TpOffline\ci-check-output.txt'
$raw = Get-Content "$env:TEMP\runs.json" -Raw -Encoding UTF8
$runs = $raw | ConvertFrom-Json
$runs.workflow_runs | Group-Object head_branch | ForEach-Object {
    $latest = $_.Group | Sort-Object created_at | Select-Object -Last 1
    "{0,-12} {1,-9} {2,-10} {3}" -f $latest.head_branch, $latest.status, $latest.conclusion, $latest.id
} | Sort-Object | Set-Content -Path $out -Encoding UTF8
