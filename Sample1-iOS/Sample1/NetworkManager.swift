//
//  NetworkManager.swift
//  Sample1
//

import UIKit

class NetworkManager: NSObject {
    class var shared: NetworkManager {
        struct Static {
            static let instance = NetworkManager()
        }
        return Static.instance
    }
    
    typealias SuccessHandler = (Any?) -> Void
    typealias FailHandler = (Error?) -> Void

    let apiLink = "http://api.stackexchange.com/2.2/answers?order=desc&sort=activity&site=stackoverflow"
    let imageCache = NSCache<AnyObject, UIImage>()
    
    func loadUsers(successHandler: @escaping SuccessHandler, failHandler: @escaping FailHandler) {
        URLSession.shared.dataTask(with: URL(string: self.apiLink)!, completionHandler: { data, response, error in
            if let error = error {
                failHandler(error)
                return
            }
            
            if let response = response as? HTTPURLResponse, response.statusCode == 200, let data = data, let json = try? JSONSerialization.jsonObject(with: data, options: []) as? [String : Any], let items = json?["items"] as? [[String : Any]]  {
                var users = [User]()
                for item in items {
                    if let owner = item["owner"] as? [String: Any], let userId = owner["user_id"] as? Int, let name = owner["display_name"] as? String, let imageLink = owner["profile_image"] as? String, let reputation = owner["reputation"] as? Int, let activity = item["last_activity_date"] as? Double {
                        let user = User()
                        user.userId = userId
                        if let data = name.data(using: .utf8), let attrName = try? NSAttributedString(data: data, options: [
                            .documentType: NSAttributedString.DocumentType.html,
                            .characterEncoding: String.Encoding.utf8.rawValue
                            ], documentAttributes: nil) {
                            user.name = attrName.string
                        }
                        else {
                            user.name = name
                        }
                        user.imageLink = imageLink
                        user.activity = activity
                        user.reputation = reputation
                        users.append(user)
                    }
                }
                successHandler(users.sorted(by: { $0.name.uppercased() < $1.name.uppercased() }))
            }
            else {
                failHandler(nil)
            }
        }).resume()
    }
    
    func loadUserImage(user: User, successHandler: @escaping SuccessHandler, failHandler: @escaping FailHandler) {
        guard let imageLink = user.imageLink, let url = URL(string: imageLink) else {
            failHandler(nil)
            return
        }
        
        if let image = self.imageCache.object(forKey: imageLink as AnyObject) {
            successHandler(image)
        }
        else {
            URLSession.shared.dataTask(with: url, completionHandler: { [weak self] data, response, error in
                if let error = error {
                    failHandler(error)
                    return
                }
                
                if let response = response as? HTTPURLResponse, response.statusCode == 200, let data = data, let image = UIImage(data: data) {
                    self?.imageCache.setObject(image, forKey: imageLink as AnyObject)
                    successHandler(image)
                }
                else {
                    failHandler(nil)
                }
            }).resume()
        }
    }
}
