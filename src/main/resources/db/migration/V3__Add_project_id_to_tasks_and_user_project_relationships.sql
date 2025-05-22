-- 1. Create the project table
CREATE TABLE projects (
                          id UUID PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          code VARCHAR(10) NOT NULL UNIQUE,
                          description TEXT,
                          end_date TIMESTAMP NOT NULL,
                          complete BOOLEAN DEFAULT FALSE NOT NULL,
                          estimated_time INTEGER DEFAULT 0 NOT NULL,
                          tasks_to_complete INTEGER DEFAULT 0 NOT NULL,
                          completed_tasks INTEGER DEFAULT 0 NOT NULL,
                          elapsed_time INTEGER DEFAULT 0 NOT NULL,
                          owner_id UUID NOT NULL REFERENCES users(id)
);

-- 2. Create the project_members join table
CREATE TABLE project_members (
                                 project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                                 user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 CONSTRAINT project_user_unique UNIQUE (project_id, user_id)
);

-- 3. Alter the tasks table to add a nullable project_id column
ALTER TABLE tasks
    ADD COLUMN project_id UUID REFERENCES projects(id) ON DELETE SET NULL;
