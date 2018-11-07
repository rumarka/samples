//
//  TableViewCell.swift
//  Sample1
//

import UIKit

class TableViewCell: UITableViewCell {
    override func layoutSubviews() {
        super.layoutSubviews()
        var frame = self.imageView!.frame
        self.imageView!.frame = CGRect(x: 10, y: (frame.size.height - 40) / 2, width: 40, height: 40)
        self.imageView!.contentMode = .scaleAspectFit
        self.imageView!.layer.cornerRadius = 20
        self.imageView!.clipsToBounds = true
        frame = self.textLabel!.frame
        self.textLabel!.frame = CGRect(x: 60, y: frame.origin.y, width: self.frame.size.width - 60, height: frame.size.height)
        frame = self.detailTextLabel!.frame
        self.detailTextLabel!.frame = CGRect(x: 60, y: frame.origin.y, width: self.frame.size.width - 60, height: frame.size.height)
        self.separatorInset = UIEdgeInsets(top: 0, left: 60, bottom: 0, right: 0)
    }
}
