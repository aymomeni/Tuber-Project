//
//  ClassTableViewCell.swift
//  Tuber
//
//  Created by Anne on 12/6/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

protocol ButtonCellDelegate {
    func cellTapped(cell: ClassTableViewCell, type: String)
}

class ClassTableViewCell: UITableViewCell {

    @IBOutlet weak var classNameLabel: UILabel!
    @IBOutlet weak var messageButton: UIButton!
    @IBOutlet weak var immediateButton: UIButton!
    @IBOutlet weak var scheduledButton: UIButton!
    @IBOutlet weak var hotspotButton: UIButton!
    
    var buttonDelegate: ButtonCellDelegate?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    @IBAction func messageButtonPress(_ sender: Any) {
        if let delegate = buttonDelegate {
            delegate.cellTapped(cell: self, type: "Message")
        }
    }

    @IBAction func scheduleButtonPress(_ sender: Any) {
        if let delegate = buttonDelegate {
            delegate.cellTapped(cell: self, type: "Schedule")
        }
    }
    
    @IBAction func hotspotButtonPress(_ sender: Any) {
        if let delegate = buttonDelegate {
            delegate.cellTapped(cell: self, type: "Hotspot")
        }
    }
}
