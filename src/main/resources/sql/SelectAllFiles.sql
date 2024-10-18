SELECT *
FROM FILE
WHERE ARRAY_CONTAINS(?, SCAN_ID)
  AND CHECKSUM IS NULL
  AND IS_DIR = FALSE
/*
Order:
    1. large size first for better performance by parallel processing
    2. FILE_ID to guarantee a deterministic order
*/
ORDER BY SIZE DESC, FILE_ID