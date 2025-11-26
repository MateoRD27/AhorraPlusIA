-- Eliminar constraint existente si ya está creado
ALTER TABLE expenses
    DROP CONSTRAINT IF EXISTS expenses_method_check;

-- Crear nuevo constraint permitido
ALTER TABLE expenses
    ADD CONSTRAINT expenses_method_check
        CHECK (method IN ('EFECTIVO', 'TRANSFERENCIA', 'DEBITO', 'TARJETA_CREDITO'));
