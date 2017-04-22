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

    //Student
    @IBOutlet weak var classNameLabel: UILabel!
    @IBOutlet weak var messageButton: UIButton!
    @IBOutlet weak var immediateButton: UIButton!
    @IBOutlet weak var scheduledButton: UIButton!
    @IBOutlet weak var hotspotButton: UIButton!
    
    
    //Tutor
    @IBOutlet weak var tutorClassNameLabel: UILabel!
    @IBOutlet weak var tutorMessageButton: UIButton!
    @IBOutlet weak var tutorImmediateButton: UIButton!
    @IBOutlet weak var tutorScheduledButton: UIButton!
    
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
    
    @IBAction func tutorMessageButtonPress(_ sender: Any) {
        if let delegate = buttonDelegate {
            delegate.cellTapped(cell: self, type: "Message")
        }
    }
    
    @IBAction func tutorImmediateButtonPress(_ sender: Any) {
        if let delegate = buttonDelegate {
            delegate.cellTapped(cell: self, type: "Immediate")
        }
    }
    
    @IBAction func tutorScheduleButtonPress(_ sender: Any) {
        if let delegate = buttonDelegate {
            delegate.cellTapped(cell: self, type: "Schedule")
        }
    }
    
}
