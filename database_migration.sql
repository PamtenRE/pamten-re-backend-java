-- Database Migration Script
-- Add missing columns to Resumes table

USE Recruitment_Platform;
GO

-- Add missing columns to Resumes table
ALTER TABLE Resumes 
ADD is_default BIT DEFAULT 0;

ALTER TABLE Resumes 
ADD custom_name VARCHAR(255);

-- Add index for better performance
CREATE INDEX idx_resumes_default ON Resumes(is_default);

-- Update existing resumes to have at least one default (if any exist)
UPDATE Resumes 
SET is_default = 1 
WHERE resume_id IN (
    SELECT TOP 1 resume_id 
    FROM Resumes r2 
    WHERE r2.candidate_id = Resumes.candidate_id 
    ORDER BY upload_date DESC
);

PRINT 'Database migration completed successfully!'; 