INSERT INTO Document(number, content, creator_id, status, title, created_at, changed_at)
VALUES ('0', 'fake fancy content', 1, 'DRAFT', 'Title one', TIMESTAMP '2017-01-01 10:44:00', TIMESTAMP '2017-01-02 10:44:00');

INSERT INTO Document(number, content, creator_id, status, title, created_at, changed_at)
VALUES ('1', 'second fake content', 2, 'ARCHIVED', 'Title fancy two', TIMESTAMP '2017-01-01 10:54:00', TIMESTAMP '2017-01-02 10:45:00');

INSERT INTO Document(number, content, creator_id, status, title, created_at, changed_at)
VALUES ('fancy', 'third fake content', 3, 'DRAFT', 'Title three', TIMESTAMP '2017-01-02 10:44:00', TIMESTAMP '2017-01-03 10:44:00');

INSERT INTO Document(number, content, creator_id, status, title, created_at, changed_at)
VALUES ('3', 'fourth fake content', 3, 'DRAFT', 'Title four', TIMESTAMP '2017-01-05 10:44:00', TIMESTAMP '2017-01-05 10:44:00');