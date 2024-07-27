UPDATE file d
SET (SIZE, FILES_CONTAINED, DIRS_CONTAINED) = (
    SELECT SUM(SIZE)                                        SIZE,
           SUM(CASE WHEN f.IS_DIR = FALSE THEN 1 ELSE 0 END) FILES_CONTAINED,
           SUM(CASE WHEN f.IS_DIR = TRUE THEN 1 ELSE 0 END) DIRS_CONTAINED
    FROM file f
    WHERE f.dir_id = d.FILE_ID
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.SIZE)
    )
WHERE d.SIZE IS NULL
  AND d.file_id IN (
    SELECT f.DIR_ID
    FROM file f
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.SIZE)
    )
