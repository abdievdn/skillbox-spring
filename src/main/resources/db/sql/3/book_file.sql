insert into book_file_type (id, description, name) values (1, 'PDF', 'pdf');
insert into book_file_type (id, description, name) values (2, 'EPUB', 'epub');
insert into book_file_type (id, description, name) values (3, 'FB2', 'fb2');
ALTER SEQUENCE book_file_type_id_seq RESTART WITH 4;

insert into book_file (id, hash, type_id, path, book_id) values (1, 'sghdj72638', 1, '/Blueberry.pdf', 396);
insert into book_file (id, hash, type_id, path, book_id) values (2, 'bdwst03754', 2, '/Blueberry.epub', 396);
insert into book_file (id, hash, type_id, path, book_id) values (3, 'sfpje24899', 3, '/Blueberry.fb2', 396);
ALTER SEQUENCE book_file_id_seq RESTART WITH 4;