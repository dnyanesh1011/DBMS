-- BEFORE DELETE trigger
CREATE OR REPLACE TRIGGER trg_before_delete_books
BEFORE DELETE ON Books
FOR EACH ROW
BEGIN
    INSERT INTO Library_Audit (AccNo, Title, Author, Publisher, Count, Action_Type, Action_Date)
    VALUES (:OLD.AccNo, :OLD.Title, :OLD.Author, :OLD.Publisher, :OLD.Count, 'DELETE', SYSDATE);
END;
/

-- AFTER UPDATE trigger
CREATE OR REPLACE TRIGGER trg_after_update_books
AFTER UPDATE ON Books
FOR EACH ROW
BEGIN
    INSERT INTO Library_Audit (AccNo, Title, Author, Publisher, Count, Action_Type, Action_Date)
    VALUES (:NEW.AccNo, :NEW.Title, :NEW.Author, :NEW.Publisher, :NEW.Count, 'UPDATE', SYSDATE);
END;
/
