CREATE OR REPLACE FUNCTION order_strat_relationship()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM stratigraphic_relationship
               WHERE fk_recording_unit_1_id = NEW.fk_recording_unit_2_id
                 AND fk_recording_unit_2_id = NEW.fk_recording_unit_1_id) THEN
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS prevent_reverse_relationship ON stratigraphic_relationship;

CREATE TRIGGER prevent_reverse_relationship
    BEFORE INSERT ON stratigraphic_relationship
    FOR EACH ROW
EXECUTE FUNCTION order_strat_relationship();