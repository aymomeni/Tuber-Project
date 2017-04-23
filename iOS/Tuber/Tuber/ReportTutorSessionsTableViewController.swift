//
//  ReportTutorSessionsTableViewController.swift
//  Tuber
//
//  Created by Anne on 3/28/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ReportTutorSessionsTableViewController: UITableViewController {

    // Set on ReportTutorListTableViewController
    var postParameters: [String] = []
    var sessionStartTime: [String] = []
    var sessionID: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = UIColor.lightGray
        self.tableView.separatorStyle = .none
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return sessionStartTime.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "tutorSessions", for: indexPath) as! ReportTutorListTableViewCell
        
        cell.datetimeLabel.text = sessionStartTime[indexPath.row]
        
        // Creates separation between cells
        cell.contentView.backgroundColor = UIColor.lightGray
        let whiteRoundedView : UIView = UIView(frame: CGRect(x: 10, y: 10, width: self.view.frame.size.width - 20, height: 70))
        whiteRoundedView.layer.backgroundColor = CGColor(colorSpace: CGColorSpaceCreateDeviceRGB(), components: [1.0, 1.0, 1.0, 1.0])
        whiteRoundedView.layer.masksToBounds = false
        whiteRoundedView.layer.cornerRadius = 3.0
        whiteRoundedView.layer.shadowOffset = CGSize(width: -1, height: 1)
        whiteRoundedView.layer.shadowOpacity = 0.5
        cell.contentView.addSubview(whiteRoundedView)
        cell.contentView.sendSubview(toBack: whiteRoundedView)
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        let session = sessionID[(indexPath?.row)!]
        
        var params = postParameters[0]
        params += "\",\"tutorSessionID\":\"\(session)\",\"message\":"
        
        self.performSegue(withIdentifier: "loadReportForm", sender: params)
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "loadReportForm"
        {
            let parameter = sender as! String
            print(parameter)
            
            if let destination = segue.destination as? ReportTutorFormViewController
            {
                destination.tempPostParameters = ""
                destination.tempPostParameters = parameter
            }
        }
    }
    
}
