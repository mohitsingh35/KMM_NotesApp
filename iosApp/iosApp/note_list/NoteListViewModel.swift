//
//  NoteListViewModel.swift
//  iosApp
//
//  Created by Mohit on 22/02/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

extension NoteListScreen {
    @MainActor class NoteListViewModel: ObservableObject {
        private var noteDataSource: NoteDataSource? = nil
        
        private let searchNotes = SearchNotes()
        
        private var notes = [Note]()
        @Published private(set) var filteredNotes = [Note]()
        @Published var searchText = "" {
            didSet {
                self.filteredNotes = searchNotes.execute(notes: self.notes, query: searchText)
            }
        }
        @Published private(set) var isSearchActive = false
        
        init(noteDataSource: NoteDataSource? = nil) {
            self.noteDataSource = noteDataSource
        }
        
        func loadNotes() {
            Task {
                do {
                    let notes : [Note] = try await withCheckedThrowingContinuation { continuation in
                        DispatchQueue.main.async {
                            Task {
                                do {
                                    let result = try await FirebaseManager().getAllNotes()
                                    continuation.resume(returning: result)
                                } catch {
                                    continuation.resume(throwing: error)
                                }
                            }
                        }
                    }

                    DispatchQueue.main.async {
                        for note in notes {
                            self.noteDataSource?.insertNote(
                                note: Note(id: note.id, title: note.title, content: note.content, colorHex: note.colorHex, created: note.created), completionHandler: { error in
                                })
                        }
                        self.noteDataSource?.getAllNotes(completionHandler: { notes, error in
                            self.notes = notes ?? []
                            self.filteredNotes = self.notes
                        })
                    }
                }
            }
            
        }
        
        func deleteNoteById(id: Int64?) {
            if id != nil {
                noteDataSource?.deleteNoteById(id: id!, completionHandler: { error in
                    self.loadNotes()
                })
            }
        }
        
        func toggleIsSearchActive() {
            isSearchActive = !isSearchActive
            if !isSearchActive {
                searchText = ""
            }
        }
        
        func setNoteDataSource(noteDataSource: NoteDataSource) {
            self.noteDataSource = noteDataSource
        }
    }
}
