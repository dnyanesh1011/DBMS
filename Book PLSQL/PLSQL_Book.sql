SET SERVEROUTPUT ON;

DECLARE
    v_roll_no       Borrower.Roll_no%TYPE := &Roll_no;
    v_book_name     Borrower.NameofBook%TYPE := '&BookName';

    v_date_of_issue Borrower.DateofIssue%TYPE;
    v_status        Borrower.Status%TYPE;
    v_days          NUMBER;
    v_fine_amt      NUMBER := 0;

    e_not_found EXCEPTION;

BEGIN
    SELECT DateofIssue, Status 
    INTO v_date_of_issue, v_status
    FROM Borrower
    WHERE Roll_no = v_roll_no AND NameofBook = v_book_name;

    IF v_status = 'R' THEN
        DBMS_OUTPUT.PUT_LINE('Book already returned.');
        RAISE e_not_found;
    END IF;

    v_days := TRUNC(SYSDATE - v_date_of_issue);
    DBMS_OUTPUT.PUT_LINE('Number of days since issue: ' || v_days);

    IF v_days > 30 THEN
        v_fine_amt := v_days * 50;
    ELSIF v_days BETWEEN 15 AND 30 THEN
        v_fine_amt := v_days * 5;
    ELSE
        v_fine_amt := 0;
    END IF;

    UPDATE Borrower
    SET Status = 'R'
    WHERE Roll_no = v_roll_no AND NameofBook = v_book_name;

    IF v_fine_amt > 0 THEN
        INSERT INTO Fine (Roll_no, FineDate, Amt)
        VALUES (v_roll_no, SYSDATE, v_fine_amt);
        DBMS_OUTPUT.PUT_LINE('Fine of Rs ' || v_fine_amt || ' added to Fine table.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('No fine applicable.');
    END IF;

    COMMIT;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No record found for given Roll number or Book name.');

    WHEN e_not_found THEN
        DBMS_OUTPUT.PUT_LINE('Custom Exception: The book was already returned.');

    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('An unexpected error occurred: ' || SQLERRM);
END;
/
