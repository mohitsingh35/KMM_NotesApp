import SwiftUI
import shared
import Firebase

@main
struct iOSApp: App {
    @State private var isLoggedIn = false
    
    init(){
        FirebaseApp.configure()
      }

    var body: some Scene {
        WindowGroup {
            NavigationView {
                if !isLoggedIn {
                    contentView(isLoggedIn: $isLoggedIn)
                } else {
                    NoteListScreen(noteDataSource: DatabaseModule().noteDataSource)
                }
            }
        }
    
    }
}

struct contentView: View {
    @State private var email = ""
    @State private var password = ""
    @Binding var isLoggedIn: Bool
    @State private var isLoading = false
    @State private var showToast = false
    @State private var toastMessage = ""
    let databaseModule=DatabaseModule()

    var body: some View {
        VStack {
            Spacer()

                            NavigationLink(
                                destination: NoteListScreen(noteDataSource: databaseModule.noteDataSource),
                                isActive: $isLoggedIn,
                                label: {
                                    EmptyView()
                                }
                            )
                            .hidden() 
            TextField("Email", text: $email)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())

            SecureField("Password", text: $password)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())

            Button(action: {
                performLogin()
            }) {
                if isLoading {
                    ProgressView()
                } else {
                    Text("Login")
                }
            }
            .padding()
            .disabled(isLoading)

        }
        .padding()
        .onAppear {
        }.overlay(
            NotificationBanner(isPresented: $showToast, message: toastMessage)
        )

    }

    func performLogin() {
        Task {
            do {
                let isSuccess = try await withCheckedThrowingContinuation { continuation in
                    DispatchQueue.main.async {
                        Task {
                            do {
                                let result = try await FirebaseManager().signInWithEmail(email: email, password: password)
                                continuation.resume(returning: result)
                            } catch {
                                continuation.resume(throwing: error)
                            }
                        }
                    }
                }

                DispatchQueue.main.async {
                    handleLoginResult(result: Bool(isSuccess))
                }
            } catch {
                showToast = true
                toastMessage = "An error occurred: \(error.localizedDescription)"
            }
        }
    }



        func handleLoginResult(result: Bool) {
            let databaseModule = DatabaseModule()
            if(result) {
                isLoggedIn = true
                showToast = true
                toastMessage = "Login Successful"

                
            } else {
                showToast = true
                toastMessage = "Login Failed"
            }
        }
    }

    struct NotificationBanner: View {
        let isPresented: Binding<Bool>
        let message: String

        var body: some View {
            VStack {
                if isPresented.wrappedValue {
                    Text(message)
                        .padding()
                        .foregroundColor(.white)
                        .background(Color.black)
                        .cornerRadius(10)
                        .transition(.move(edge: .top))
                        .animation(.easeInOut(duration: 0.5))
                        .onTapGesture {
                            withAnimation {
                                isPresented.wrappedValue = false
                            }
                        }
                }
            }
        }
    }
