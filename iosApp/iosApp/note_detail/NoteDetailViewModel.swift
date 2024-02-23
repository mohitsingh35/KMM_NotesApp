//
//  NoteDetailViewModel.swift
//  iosApp
//
//  Created by Mohit on 22/02/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

extension NoteDetailScreen {
    @MainActor class NoteDetailViewModel: ObservableObject {
        private var noteDataSource: NoteDataSource?
        
        private var noteId: Int64? =  Int64(Int(arc4random_uniform(100000))) + 1
        @Published var noteTitle = ""
        @Published var noteContent = ""
        @Published private(set) var noteColor = Note.Companion().generateRandomColor()
        
        init(noteDataSource: NoteDataSource? = nil) {
            self.noteDataSource = noteDataSource
        }
        
        func loadNoteIfExists(id: Int64?) {
            if id != nil {
                self.noteId = id
                
                noteDataSource?.getNotebyId(id: id!, completionHandler: { note, error in
                    self.noteTitle = note?.title ?? ""
                    self.noteContent = note?.content ?? ""
                    self.noteColor = note?.colorHex ?? Note.Companion().generateRandomColor()
                })
            }
        }
        
        func saveNote(onSaved: @escaping () -> Void) {
            Task {
                do {
                    let isSuccess = try await withCheckedThrowingContinuation { continuation in
                        DispatchQueue.main.async {
                            Task {
                                do {
                                    let result = try await FirebaseManager().upsert(note: Note(id: self.noteId == nil ? nil : KotlinLong(value: self.noteId!), title: self.noteTitle, content: self.noteContent, colorHex: self.noteColor, created: DateTimeUtil().now()))
                                    continuation.resume(returning: result)
                                } catch {
                                    continuation.resume(throwing: error)
                                }
                            }
                        }
                    }

                    DispatchQueue.main.async {
                        if(Bool(isSuccess)){
                            self.noteDataSource?.insertNote(
                                note: Note(id: self.noteId == nil ? nil : KotlinLong(value: self.noteId!), title: self.noteTitle, content: self.noteContent, colorHex: self.noteColor, created: DateTimeUtil().now()), completionHandler: { error in
                                    onSaved()
                                })
                        }
                    }
                }
            }

            
        }
        
        func setParamsAndLoadNote(noteDataSource: NoteDataSource, noteId: Int64?) {
            self.noteDataSource = noteDataSource
            loadNoteIfExists(id: noteId)
        }
    }
}
