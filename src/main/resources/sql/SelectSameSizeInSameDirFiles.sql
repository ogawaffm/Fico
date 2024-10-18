SELECT *
FROM FILE
WHERE ARRAY_CONTAINS(?, SCAN_ID)
  AND CHECKSUM IS NULL
  AND IS_DIR = FALSE
  AND (DIR_ID, SIZE) IN (
    SELECT DIR_ID, SIZE
    FROM FILE
    GROUP BY DIR_ID, SIZE
    HAVING COUNT(*) > 1
    )
/*
Order:
    1. large size first for better performance by parallel processing
    2. DIR_ID to keep files in the same directory together for human readability
    3. FILE_ID to guarantee a deterministic order
*/
ORDER BY SIZE DESC, DIR_ID, FILE_ID