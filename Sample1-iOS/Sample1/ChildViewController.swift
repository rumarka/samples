//
//  ChildViewController.swift
//  Sample1
//

import UIKit

class ChildViewController: UIViewController {
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var label: UILabel!
    @IBOutlet weak var viewHeightConstraint: NSLayoutConstraint!
    
    var user: User!
    var image: UIImage!
    
    private let formatter = DateFormatter()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.configureView()
        self.showData()
    }

    private func configureView() {
        self.formatter.dateStyle = .short
        self.formatter.timeStyle = .short
        self.formatter.doesRelativeDateFormatting = true
    }
    
    private func showData() {
        if let image = self.image {
            imageView.image = image
        }
        
        if let user = self.user {
            let attrText = NSMutableAttributedString(string: user.name, attributes: [NSAttributedStringKey.font : UIFont.boldSystemFont(ofSize: 19)])
            attrText.append(NSAttributedString(string: "\nReputation: \(user.reputation!)\nLast activity at \(formatter.string(from: Date(timeIntervalSince1970: user.activity)))", attributes: [NSAttributedStringKey.font : UIFont.systemFont(ofSize: 15)]))
            attrText.append(NSAttributedString(string: "\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            label.attributedText = attrText
            label.sizeToFit()
        }
        
        self.viewHeightConstraint.constant = imageView.frame.size.height + label.frame.size.height + 87
    }
 }
