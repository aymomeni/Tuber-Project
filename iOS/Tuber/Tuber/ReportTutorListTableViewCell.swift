//
//  ReportTutorListTableViewCell.swift
//  Tuber
//
//  Created by Anne on 3/2/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ReportTutorListTableViewCell: UITableViewCell {

    
    @IBOutlet weak var tutorNameLabel: UILabel!
    
    @IBOutlet weak var dateLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
