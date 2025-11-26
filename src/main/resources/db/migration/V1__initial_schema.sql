
-- MIGRACIÓN V1: Estructura inicial de la base de datos

-- Tabla: users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     fixed_salary DOUBLE PRECISION,
                                     current_available_money DOUBLE PRECISION,
                                     registration_date TIMESTAMP NOT NULL,
                                     account_status VARCHAR(50) NOT NULL,
                                     verification_token VARCHAR(255),
                                     verification_token_expiration TIMESTAMP,
                                     verified_account BOOLEAN NOT NULL DEFAULT FALSE,
                                     last_access_date TIMESTAMP,
                                     creation_date TIMESTAMP NOT NULL,
                                     modification_date TIMESTAMP
);

-- Tabla: roles
CREATE TABLE IF NOT EXISTS roles (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla: user_roles (relación muchos a muchos)
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id INTEGER NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                          FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabla: logins
CREATE TABLE IF NOT EXISTS logins (
                                      id BIGSERIAL PRIMARY KEY,
                                      login_date TIMESTAMP NOT NULL,
                                      logout_date TIMESTAMP,
                                      ip_address VARCHAR(45),
                                      successful BOOLEAN NOT NULL,
                                      failure_reason VARCHAR(255),
                                      user_id BIGINT NOT NULL,
                                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: income (ingresos)
CREATE TABLE IF NOT EXISTS income (
                                      id_income SERIAL PRIMARY KEY,
                                      amount DECIMAL(15, 2) NOT NULL,
                                      date DATE NOT NULL,
                                      source VARCHAR(100),
                                      description TEXT,
                                      creation_date TIMESTAMP NOT NULL,
                                      modification_date TIMESTAMP,
                                      user_id BIGINT NOT NULL,
                                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: expenses (gastos)
CREATE TABLE IF NOT EXISTS expenses (
                                        id_expense SERIAL PRIMARY KEY,
                                        amount DECIMAL(15, 2) NOT NULL,
                                        date DATE NOT NULL,
                                        method VARCHAR(50) NOT NULL,
                                        description TEXT,
                                        anomalous BOOLEAN NOT NULL DEFAULT FALSE,
                                        overlimit BOOLEAN NOT NULL DEFAULT FALSE,
                                        creation_date TIMESTAMP NOT NULL,
                                        modification_date TIMESTAMP,
                                        user_id BIGINT NOT NULL,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: savings_goals (metas de ahorro)
CREATE TABLE IF NOT EXISTS savings_goals (
                                             id_goal SERIAL PRIMARY KEY,
                                             name VARCHAR(150) NOT NULL,
                                             target_amount DECIMAL(15, 2) NOT NULL,
                                             current_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
                                             start_date DATE NOT NULL,
                                             end_date DATE NOT NULL,
                                             priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
                                             frequency VARCHAR(50) NOT NULL,
                                             suggested_quota DECIMAL(15, 2),
                                             status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                                             deletable BOOLEAN NOT NULL DEFAULT TRUE,
                                             days_to_delete INTEGER,
                                             creation_date TIMESTAMP NOT NULL,
                                             modification_date TIMESTAMP,
                                             user_id BIGINT NOT NULL,
                                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: notifications
CREATE TABLE IF NOT EXISTS notifications (
                                             id_notification SERIAL PRIMARY KEY,
                                             type VARCHAR(50) NOT NULL,
                                             message TEXT NOT NULL,
                                             sent_date TIMESTAMP NOT NULL,
                                             read BOOLEAN NOT NULL DEFAULT FALSE,
                                             user_id BIGINT NOT NULL,
                                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: settings (configuraciones de usuario)
CREATE TABLE IF NOT EXISTS settings (
                                        id_setting SERIAL PRIMARY KEY,
                                        notification_frequency VARCHAR(50) NOT NULL DEFAULT 'MONTHLY',
                                        notification_channels VARCHAR(100),
                                        budget_alerts_active BOOLEAN NOT NULL DEFAULT TRUE,
                                        spending_alert_threshold DECIMAL(15, 2),
                                        currency VARCHAR(10) NOT NULL DEFAULT 'COP',
                                        user_id BIGINT NOT NULL UNIQUE,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: support_tickets
CREATE TABLE IF NOT EXISTS support_tickets (
                                               id_ticket SERIAL PRIMARY KEY,
                                               request_type VARCHAR(50) NOT NULL,
                                               description TEXT NOT NULL,
                                               status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
                                               priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
                                               creation_date TIMESTAMP NOT NULL,
                                               resolution_date TIMESTAMP,
                                               admin_response TEXT,
                                               id_admin INTEGER,
                                               user_id BIGINT NOT NULL,
                                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: change_history (historial de cambios)
CREATE TABLE IF NOT EXISTS change_history (
                                              id_history SERIAL PRIMARY KEY,
                                              entity_type VARCHAR(50) NOT NULL,
                                              id_entity INTEGER NOT NULL,
                                              action VARCHAR(50) NOT NULL,
                                              old_data TEXT,
                                              new_data TEXT,
                                              change_date TIMESTAMP NOT NULL,
                                              user_id BIGINT NOT NULL,
                                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: reports
CREATE TABLE IF NOT EXISTS reports (
                                       id_report SERIAL PRIMARY KEY,
                                       report_type VARCHAR(50) NOT NULL,
                                       start_period DATE NOT NULL,
                                       end_period DATE NOT NULL,
                                       format VARCHAR(50) NOT NULL,
                                       generation_date TIMESTAMP NOT NULL,
                                       applied_filters TEXT,
                                       user_id BIGINT NOT NULL,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla: recomendacion (inicialmente con VARCHAR(255))
CREATE TABLE IF NOT EXISTS recomendacion (
                                             id BIGSERIAL PRIMARY KEY,
                                             message VARCHAR(255),
                                             status VARCHAR(50) NOT NULL DEFAULT 'SUGERIDA',
                                             creation_date TIMESTAMP NOT NULL,
                                             fecha_evaluacion TIMESTAMP,
                                             was_useful BOOLEAN,
                                             id_usuario BIGINT NOT NULL,
                                             FOREIGN KEY (id_usuario) REFERENCES users(id) ON DELETE CASCADE
);

-- Insertar roles por defecto
INSERT INTO roles (name) VALUES ('ROLE_USER') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_income_user_id ON income(user_id);
CREATE INDEX IF NOT EXISTS idx_income_date ON income(date);
CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON expenses(user_id);
CREATE INDEX IF NOT EXISTS idx_expenses_date ON expenses(date);
CREATE INDEX IF NOT EXISTS idx_savings_goals_user_id ON savings_goals(user_id);
CREATE INDEX IF NOT EXISTS idx_recomendacion_user_id ON recomendacion(id_usuario);
CREATE INDEX IF NOT EXISTS idx_recomendacion_status ON recomendacion(status);