CREATE
    OR REPLACE FUNCTION insert_log()
    RETURNS TRIGGER
AS
$$
BEGIN
    INSERT INTO log_entry(fk_user_id, log_date, message, fk_ark_id)
    VALUES (NEW.fk_author_id, NOW(), TG_ARGV[0], NEW.fk_ark_id);
    RETURN NEW;
END
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_deletion_log()
    RETURNS TRIGGER
AS
$$
BEGIN
    INSERT INTO log_entry(fk_user_id, log_date, message, fk_ark_id)
    VALUES (OLD.fk_author_id, NOW(), 'DELETION', OLD.fk_ark_id);
    RETURN NEW;
end
$$
    LANGUAGE plpgsql;

-- Triggers for action_unit
CREATE OR REPLACE TRIGGER t_insert_action_unit_log
    AFTER INSERT
    ON action_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_action_unit_log
    AFTER UPDATE
    ON action_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_action_unit_log
    BEFORE DELETE
    ON action_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();

-- Triggers for recording_unit
CREATE OR REPLACE TRIGGER t_insert_recording_unit_log
    AFTER INSERT
    ON recording_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_recording_unit_log
    AFTER UPDATE
    ON recording_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_recording_unit_log
    BEFORE DELETE
    ON recording_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();

-- Triggers for recording_unit_study
CREATE OR REPLACE TRIGGER t_insert_recording_unit_study_log
    AFTER INSERT
    ON recording_unit_study
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_recording_unit_study_log
    AFTER UPDATE
    ON recording_unit_study
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_recording_unit_study_log
    BEFORE DELETE
    ON recording_unit_study
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();

-- Triggers for specimen_study
CREATE OR REPLACE TRIGGER t_insert_specimen_study_log
    AFTER INSERT
    ON specimen_study
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_specimen_study_log
    AFTER UPDATE
    ON specimen_study
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_specimen_study_log
    BEFORE DELETE
    ON specimen_study
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();

-- Triggers for specimen
CREATE OR REPLACE TRIGGER t_insert_specimen_log
    AFTER INSERT
    ON specimen
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_specimen_log
    AFTER UPDATE
    ON specimen
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_specimen_log
    BEFORE DELETE
    ON specimen
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();

-- Triggers for siamois_document
CREATE OR REPLACE TRIGGER t_insert_siamois_document_log
    AFTER INSERT
    ON siamois_document
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_siamois_document_log
    AFTER UPDATE
    ON siamois_document
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_siamois_document_log
    BEFORE DELETE
    ON siamois_document
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();

-- Triggers for spatial_unit
CREATE OR REPLACE TRIGGER t_insert_spatial_unit_log
    AFTER INSERT
    ON spatial_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_log('CREATION');

CREATE OR REPLACE TRIGGER t_update_spatial_unit_log
    AFTER UPDATE
    ON spatial_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_log('UPDATE');

CREATE OR REPLACE TRIGGER t_delete_spatial_unit_log
    BEFORE DELETE
    ON spatial_unit
    FOR EACH ROW
EXECUTE FUNCTION insert_deletion_log();



