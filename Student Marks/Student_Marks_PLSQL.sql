SET SERVEROUTPUT ON;

DECLARE
    v_r   NUMBER;
    total NUMBER;
    nm    VARCHAR2(50);
    gr    VARCHAR2(50);
BEGIN
    FOR rec IN (SELECT roll_no, name, total_mark FROM student_mark) LOOP
        v_r   := rec.roll_no;
        nm    := rec.name;
        total := rec.total_mark;
        proc_grade(v_r, total, gr);

        DBMS_OUTPUT.PUT_LINE(v_r || ' ' || nm || ' ' || gr);

        INSERT INTO student_result(roll_no, name, grade)
        VALUES (v_r, nm, gr);
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('All records processed and inserted');
END;
/
