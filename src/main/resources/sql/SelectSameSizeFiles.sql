SELECT *
FROM FILE
WHERE ARRAY_CONTAINS(?, SCAN_ID)
  AND CHECKSUM IS NULL
  AND IS_DIR = FALSE
  AND (SIZE) IN (
    SELECT SIZE
    FROM FILE
    GROUP BY SIZE
    HAVING COUNT(*) > 1
    )
/*
Order:
    1. large size first for better performance by parallel processing
    2. NAME to keep the same name files together
    3. FILE_ID to guarantee a deterministic order
*/
ORDER BY SIZE DESC, NAME, FILE_ID