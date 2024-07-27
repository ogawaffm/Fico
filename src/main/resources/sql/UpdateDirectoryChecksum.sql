UPDATE file d
SET CHECKSUM = (
    SELECT cast(HASH('SHA-256', listagg(rawtohex(f.CHECKSUM), '') WITHIN GROUP (ORDER BY f.name)) as varbinary(32)) CHECKSUM
    FROM file f
    WHERE f.dir_id = d.FILE_ID
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.CHECKSUM)
       and COUNT(*) > 0
    )
WHERE d.IS_DIR = TRUE
  AND d.CHECKSUM IS NULL
  and d.file_id in (
    SELECT f.DIR_ID
    FROM file f
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.CHECKSUM)
       AND COUNT(*) > 0
    )
