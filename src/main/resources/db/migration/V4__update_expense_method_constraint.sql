ALTER TABLE expenses
    DROP CONSTRAINT IF EXISTS expenses_method_check;

ALTER TABLE expenses
    ADD CONSTRAINT expenses_method_check
        CHECK (method IN ('EFECTIVO','TRANSFERENCIA','DEBITO','TARJETA_CREDITO'));
