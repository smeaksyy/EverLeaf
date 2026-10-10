package com.everleaf.dao;

import com.everleaf.model.Note;
import java.util.List;

public interface NoteDAO {
    boolean addNote(Note note);
    List<Note> getNotesByUser(int userId);
    Note getNoteById(int noteId, int userId);
    boolean updateNote(Note note);
    boolean deleteNote(int noteId, int userId);
    int getNoteCountByUser(int userId);
}
