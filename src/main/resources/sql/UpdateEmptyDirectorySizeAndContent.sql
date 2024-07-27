UPDATE file d
SET (SIZE, FILES_CONTAINED, DIRS_CONTAINED) = (0, 0, 0)
WHERE d.SIZE IS NULL
  AND d.file_id NOT IN (
    SELECT f.DIR_ID
    FROM file f
    WHERE f.dir_id = d.FILE_ID
    GROUP BY f.dir_id
)