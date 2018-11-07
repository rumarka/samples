//
//  ViewController.swift
//  Sample1
//

import UIKit

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchResultsUpdating, UISearchBarDelegate {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var searchBarConstraint1: NSLayoutConstraint!
    @IBOutlet weak var searchBarConstraint2: NSLayoutConstraint!
    
    private var users = [User]()
    private var filteredUsers = [User]()
    private let cellId = "Cell"
    private let segueId = "Details"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.configureView()
        self.loadData()
    }

    private func configureView() {
        self.tableView.tableFooterView = UIView()

        if #available(iOS 11.0, *) {
            self.searchBarConstraint1.priority = UILayoutPriority(rawValue: 999)
            self.searchBarConstraint2.priority = UILayoutPriority(rawValue: 250)
            self.searchBar.isHidden = true
            
            let search = UISearchController(searchResultsController: nil)
            search.searchResultsUpdater = self
            search.dimsBackgroundDuringPresentation = false
            self.navigationItem.searchController = search
            self.navigationItem.hidesSearchBarWhenScrolling = false
        } else {
            self.searchBarConstraint1.priority = UILayoutPriority(rawValue: 250)
            self.searchBarConstraint2.priority = UILayoutPriority(rawValue: 999)
            self.searchBar.isHidden = false
            self.searchBar.delegate = self
        }

    }
    
    private func loadData() {
        self.indicator.startAnimating()
        NetworkManager.shared.loadUsers(successHandler: { [weak self] users in
            guard let `self` = self else { return }
            if let users = users as? [User] {
                self.users = users
                DispatchQueue.main.async(execute: { [weak self] in
                    guard let `self` = self else { return }
                    self.reloadData()
                    self.indicator.stopAnimating()
                })
            }
            else {
                DispatchQueue.main.async(execute: { [weak self] in
                    self?.indicator.stopAnimating()
                })
            }
        }, failHandler: { [weak self] error in
            print("ERROR:", error?.localizedDescription ?? "Unknown")

            DispatchQueue.main.async(execute: { [weak self] in
                self?.indicator.stopAnimating()
            })
        })
    }
    
    private func reloadData(_ searchText: String? = nil) {
        if let text = searchText, text.count > 0 {
            self.filteredUsers = self.users.filter({ $0.name.uppercased().contains(text.uppercased()) })
        }
        else {
            self.filteredUsers = self.users
        }
        self.tableView.reloadData()
    }
    
    // MARK: - UITableViewDelegate
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let cell = tableView.cellForRow(at: indexPath) else { return }
        let user = filteredUsers[indexPath.row]
        self.performSegue(withIdentifier: self.segueId, sender: (user: user, image: cell.imageView!.image))
    }
    
    // MARK: - UITableViewDataSource
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.filteredUsers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! TableViewCell
        let user = filteredUsers[indexPath.row]
        cell.textLabel?.text = user.name
        let status = user.reputation < 100 ? "Newbie" : (user.reputation > 1000 ? "Expert" : "Member")
        cell.detailTextLabel?.text = "\(status)"
        cell.imageView?.image = UIImage(named: "PlaceholderImage")
        NetworkManager.shared.loadUserImage(user: user, successHandler: { data in
            if let image = data as? UIImage {
                DispatchQueue.main.async {
                    cell.imageView?.image = image
                }
            }
        }, failHandler: { error in
            print("ERROR:", error?.localizedDescription ?? "Unknown")
        })
        return cell
    }
    
    // MARK: - UISearchResultsUpdating
    
    func updateSearchResults(for searchController: UISearchController) {
        self.reloadData(searchController.searchBar.text)
    }
    
    // MARK: - UISearchBarDelegate
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        self.reloadData(searchText)
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        self.searchBar.resignFirstResponder()
    }
    
    // MARK: - Navigation
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == segueId, let data = sender as? (user: User, image: UIImage) {
            let controller = segue.destination as! ChildViewController
            controller.user = data.user
            controller.image = data.image
        }
    }
}

