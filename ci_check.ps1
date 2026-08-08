$ErrorActionPreference = 'Continue'
$out = 'E:\GitHub\TpOffline\ci-check-output.txt'
Set-Content -Path $out -Value "=== CI check start ==="

try {
    $raw = Get-Content "$env:TEMP\runs.json" -Raw -Encoding UTF8
    Add-Content -Path $out -Value "raw length: $($raw.Length)"
    $runs = $raw | ConvertFrom-Json
    Add-Content -Path $out -Value "type: $($runs.GetType().Name)"
    Add-Content -Path $out -Value "workflow_runs count: $($runs.workflow_runs.Count)"
    $runs.workflow_runs | Group-Object head_branch | ForEach-Object {
        $latest = $_.Group | Sort-Object created_at | Select-Object -Last 1
        "{0,-12} {1,-9} {2,-10} {3}" -f $latest.head_branch, $latest.status, $latest.conclusion, $latest.id
    } | Sort-Object | Add-Content -Path $out
} catch {
    Add-Content -Path $out -Value "ERROR: $($_.Exception.Message)"
}
