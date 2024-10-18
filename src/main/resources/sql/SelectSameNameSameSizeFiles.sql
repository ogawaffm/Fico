SELECT F.*
FROM FILE F
         JOIN (SELECT NAME, SIZE
               FROM FILE
               WHERE IS_DIR = FALSE
                 AND ARRAY_CONTAINS(?, SCAN_ID)
               GROUP BY SIZE, NAME
               HAVING COUNT(*) > 1
                  AND SUM(CASE WHEN CHECKSUM IS NULL THEN 1 END) > 0) DUPLICATE
              ON F.NAME = DUPLICATE.NAME AND F.SIZE = DUPLICATE.SIZE
/*
Order:
    1. large size first for better performance by parallel processing
    2. NAME to keep the same name files together
    3. FILE_ID to guarantee a deterministic order
*/
ORDER BY SIZE DESC, NAME, FILE_ID