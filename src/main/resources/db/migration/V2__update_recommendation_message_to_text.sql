-- Cambiar el tipo de columna message a TEXT para soportar textos largos de IA
ALTER TABLE recomendacion
    ALTER COLUMN message TYPE TEXT;

-- Agregar comentario descriptivo
COMMENT ON COLUMN recomendacion.message IS 'Mensaje de recomendación generado por IA (Gemini). Campo TEXT para soportar respuestas largas.';