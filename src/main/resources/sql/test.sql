select *
    from file d
join (
    SELECT f.dir_id, cast(HASH('SHA-256', listagg(rawtohex(f.CHECKSUM), '') WITHIN GROUP (ORDER BY f.name)) as varbinary(32)) sum
    FROM file f
    //WHERE f.dir_id = d.FILE_ID
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.CHECKSUM)
       and COUNT(*) > 0
    ) f
    on f.dir_id = d.FILE_ID
WHERE d.size IS NULL AND d.CHECKSUM IS NULL


select *
from file d where  NAME = 'catalog_24.png'
where d.SIZE is null and d.CHECKSUM is not null

UPDATE file d
SET CHECKSUM = (
    SELECT
        X'12' CHECKSUM
    FROM file f
    WHERE f.dir_id = d.FILE_ID
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.CHECKSUM)
       and COUNT(*) > 0
    )
WHERE d.size IS NULL
  AND d.CHECKSUM IS NULL
/*
  and d.dir_id in (
    SELECT f.DIR_ID
    FROM file f
    GROUP BY f.dir_id
    HAVING COUNT(*) = COUNT(f.CHECKSUM)
       AND COUNT(*) > 0
    )
*/


